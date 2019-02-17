import pandas as pd
import numpy as np
import scipy.stats as stats
from scipy.stats import binom


def countProtected(ranking):
    result = 0
    for candidate in ranking:
        if candidate.isProtected:
            result += 1
    return result


class AlphaAdjustment:

    def __init__(self, k: int, p: float, alpha: float):
        if k < 1:
            raise ValueError("Parameter k must be at least 1")
        if p <= 0.0 or p >= 1.0:
            raise ValueError("Parameter p must be in [0.0, 1.0]")
        if alpha <= 0.0 or alpha > 1.0:
            raise ValueError("Parameter alpha must be in [0.0, 1.0]")

        self.k = k
        self.p = p
        self.alpha = alpha

        self.mtable = self.compute_mtable()
        self.aux_mtable = self.compute_aux_mtable()

    def m(self, k: int):
        if k < 1:
            raise ValueError("Parameter k must be at least 1")
        elif k > self.k:
            raise ValueError("Parameter k must be at most n")

        return stats.binom.ppf(self.alpha, k, self.p)

    def compute_mtable(self):
        """ Computes a table containing the minimum number of protected elements
            required at each position
        """
        mtable = pd.DataFrame(columns=["m"])
        for i in range(1, self.k + 1):
            if i % 2000 == 0:
                print("Computing m: {:.0f} of {:.0f}".format(i, self.k))
            mtable.loc[i] = [self.m(i)]
        return mtable

    def compute_aux_mtable(self):
        """ Computes an auxiliary table containing the inverse table m[i] and the block sizes
        """
        if not(isinstance(self.mtable, pd.DataFrame)):
            raise TypeError("Internal mtable must be a DataFrame")

        aux_mtable = pd.DataFrame(columns=["inv", "block"])
        last_m_seen = 0
        last_position = 0
        for position in range(1, len(self.mtable)):
            if position % 2000 == 0:
                print("Computing m inverse: {:.0f} of {:.0f}".format(position, len(self.mtable)))
            if self.mtable.at[position, "m"] == last_m_seen + 1:
                last_m_seen += 1
                aux_mtable.loc[last_m_seen] = [position, position - last_position]
                last_position = position
            elif self.mtable.at[position, "m"] != last_m_seen:
                raise RuntimeError("Inconsistent mtable")

        return aux_mtable

    def compute_success_probability(self):
        max_protected = self.aux_mtable["inv"].count()
        min_protected = 1

        success_obtained_prob = np.zeros(max_protected)
        success_obtained_prob[0] = 1.0

        self.success_prob_report = pd.DataFrame(columns=["prob"])

        pmf_cache = pd.DataFrame(columns=["table"])
        while min_protected < max_protected:
            if min_protected % 2000 == 0:
                print("Computing success probability: block {:.0f} of {:.0f}".format(min_protected, max_protected))


            block_length = int(self.aux_mtable["block"][min_protected])

            if block_length in pmf_cache.index:
                current_trial = pmf_cache.loc[block_length]["table"]
            else:
                current_trial = np.empty(int(block_length) + 1)
                for i in range(0, int(block_length) + 1):
                    current_trial[i] = stats.binom.pmf(i, block_length, self.p)
                pmf_cache.loc[block_length] = [current_trial]

            new_success_obtained_prob = np.zeros(max_protected)
            for i in range(0, int(block_length) + 1):
                increase = np.roll(success_obtained_prob, i) * current_trial[i]
                new_success_obtained_prob += increase
            new_success_obtained_prob[min_protected - 1] = 0

            success_obtained_prob = new_success_obtained_prob

            success_probability = success_obtained_prob.sum()
            self.success_prob_report.loc[self.aux_mtable["inv"][min_protected]] = success_probability

            success_obtained_prob = new_success_obtained_prob

            min_protected += 1

        return success_probability

    def compute_fail_probability(self):
        return 1 - self.compute_success_probability()


class FairnessInRankingsTester():
    """
    implementation of the statistical significance binomial.test that decides if a ranking has a fair representation
    and ordering of protected candidates with respect to non-protected ones.
    The binomial.test is based on the cumulative distribution function of a binomial distribution, i.e. on a
    Bernoulli process which we believe is fair.
    A ranking is accepted as fair, if it fairly represents protected candidates over all prefixes of
    the ranking.
    To fairly represent the protected group in a prefix the binomial.test compares the actual number of protected
    candidates in the given ranking prefix to the number that would be obtained with a probability of
    p by random Bernoulli trials. If these numbers do not differ too much, the fair representation
    condition accepts this prefix. If this holds for every prefix, the entire ranking is accepted
    as fair.
    """

    @property
    def candidates_needed(self):
        return self.__candidatesNeeded

    @property
    def minimal_proportion(self):
        return self.__minProp

    def __init__(self, minProp, alpha, k, correctedAlpha):
        """
        @param minProp : float
            the minimal proportion of protected candidates that the set should have
        @param alpha : float
            significance level for the binomial cumulative distribution function -> minimum probability at
            which a fair ranking contains the minProp amount of protected candidates
        @param k : int
            the expected length of the ranked output
        @param correctedAlpha : bool
        FIXME: guck nochmal, warum man die Korrektur überhaupt nicht wollen würde
            tells if model adjustment shall be used or not
        """
        self.__minProp = minProp
        self.__alpha = alpha
        if correctedAlpha:
            self.__candidatesNeeded = self.__candidates_needed_with_correction(k)
        else:
            self.__candidatesNeeded = self.__calculate_protected_needed_at_each_position(k)

    def getCandidatesNeeded(self):
        return self.__candidatesNeeded

    def ranked_group_fairness_condition(self, ranking):
        """
        checks that every prefix of a given ranking tau satisfies the fair representation condition
        starts to check from the top of the list (i.e. with the first candidate) and expands downwards
        breaks as soon as it finds a prefix that is unfair
        Parameters:
        ----------
        ranking : [Candidate]
            the set to be checked for fair representation
        Return:
        ------
        True if the ranking has a fair representation of the protected group for each prefix
        False and the index at which the fair representation condition was not satisfied
        """

        prefix = []

        for t in range(len(ranking)):
            prefix.append(ranking[t])
            if not self.fair_representation_condition(prefix):
                return t, False

        return 0, True

    def fair_representation_condition(self, ranking):
        """
        checks if a given ranking with tau_p protected candidates fairly represents the protected group. A
        minimal proportion of protected candidates is defined in advance.
        Parameters:
        ----------
        ranking : [Candidate]
        the set to be checked for fair representation
        Return:
        ------
        True if the ranking fairly represents the protected group, False otherwise
        """

        t = len(ranking)
        numberProtected = countProtected(ranking)

        if self.__candidatesNeeded[t - 1] > numberProtected:
            # not enough protected candidates in my ranking
            return False
        else:
            return True

    def __calculate_protected_needed_at_each_position(self, k):
        result = []

        if self.__minProp == 0:
            # handle special case minProp = 0
            result = [0] * k
        else:
            for n in range(1, k + 1):
                numProtCandidates = binom.ppf(self.__alpha, n, self.__minProp)
                result.append(int(numProtCandidates))

        return result

    def __candidates_needed_with_correction(self, k):
        fc = AlphaAdjustment(k, self.__minProp, self.__alpha)
        mtableAsList = fc.mtable.m.tolist()
        mtableAsList = [int(i) for i in mtableAsList]
        return mtableAsList


# Perform binomial.test

if __name__ == '__main__':
    testerCATrue = FairnessInRankingsTester(minProp=0.5, alpha=0.01, k=1000, correctedAlpha=True)
    testerCAFalse = FairnessInRankingsTester(minProp=0.5, alpha=0.01, k=1000, correctedAlpha=False)

    print("Table with correctedAlpha=True:")
    print(testerCATrue.getCandidatesNeeded())
    print("Table with correctedAlpha=False:")
    print(testerCAFalse.getCandidatesNeeded())

    print("Both calls with correctedAlpha=True and correctedAlpha=False generate same table?")
    print(testerCAFalse.getCandidatesNeeded() == testerCATrue.getCandidatesNeeded())


    k = 1500
    alpha = 0.05
    for p in [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9]:
        a = AlphaAdjustment(k, p, alpha)
        print("Analytical Python for k=%d, p=%.2f, alpha=%.2f: %.4f" % (k, p, alpha, a.compute_fail_probability()))

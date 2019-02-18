# -*- coding: utf-8 -*-

"""
Contains the mechanics for creating an mtable
"""

import pandas as pd
import scipy.stats as stats

from fairsearchcore import fail_prob


class MTableGenerator:

    def __init__(self, k: int, p: float, alpha: float, adjust_alpha: bool):
        # assign parameters
        self.k = k
        self.p = p
        self.alpha = alpha
        self.adjust_alpha = adjust_alpha

        if self.adjust_alpha:
            fail_prob_pair = fail_prob.RecursiveNumericFailprobabilityCalculator(k, p, alpha).adjust_alpha()
            self.adjusted_alpha = fail_prob_pair.alpha
            self.mtable = fail_prob_pair.mtable
        else:
            self.adjusted_alpha = alpha
            self.mtable = self._compute_mtable()

        self.aux_mtable = self._compute_aux_mtable()

    def get_mtable(self):
        return [int(i) for i in self.mtable.m.tolist()]

    def m(self, k: int):
        if k < 1:
            raise ValueError("Parameter k must be at least 1")
        elif k > self.k:
            raise ValueError("Parameter k must be at most n")

        return stats.binom.ppf(self.alpha, k, self.p)

    def _compute_mtable(self):
        """ Computes a table containing the minimum number of protected elements
            required at each position
        """
        mtable = pd.DataFrame(columns=["m"])
        for i in range(1, self.k + 1):
            if i % 2000 == 0:
                print("Computing m: {:.0f} of {:.0f}".format(i, self.k))
            mtable.loc[i] = [self.m(i)]
        return mtable

    def _compute_aux_mtable(self):
        """ Computes an auxiliary table containing the inverse table m[i] and the block sizes
        """
        return compute_aux_mtable(self.mtable)


def compute_aux_mtable(mtable) -> pd.DataFrame:
    """
    Stores the inverse of an mTable entry and the size of the block with respect to the inverse
    """
    if not (isinstance(mtable, pd.DataFrame)):
        raise TypeError("Internal mtable must be a DataFrame")

    aux_mtable = pd.DataFrame(columns=["inv", "block"])
    last_m_seen = 0
    last_position = 0
    for position in range(1, len(mtable)):
        if position % 2000 == 0:
            print("Computing m inverse: {:.0f} of {:.0f}".format(position, len(mtable)))
        if mtable.at[position, "m"] == last_m_seen + 1:
            last_m_seen += 1
            aux_mtable.loc[last_m_seen] = [position, position - last_position]
            last_position = position
        elif mtable.at[position, "m"] != last_m_seen:
            raise RuntimeError("Inconsistent mtable")

    return aux_mtable

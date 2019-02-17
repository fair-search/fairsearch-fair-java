# -*- coding: utf-8 -*-

"""
This module serves as a wrapper around the utilities we have created for FA*IR ranking
"""
from fairsearchcore.alpha_adjustment import AlphaAdjustment

class Fair:

    k = 10 # the total number of elements
    p = 0.2 # the proportion of protected candidates in the top-k ranking
    alpha = 0.1 # the significance level

    def __init__(self, k: int, p: float, alpha: float):
        # check the parameters first
        _validate_basic_parameters(k, p, alpha)

        # assign the parameters
        self.k = k
        self.p = p
        self.alpha = alpha

    def _create_mtable(self, alpha: int, adjust_alpha: float):
        """
        Creates an mtable by, if you want, pass your own alpha value(overridng the object's one)
        :param alpha:           The significance level
        :param adjust_alpha:    Boolean indicating whether the alpha be adjusted or not
        :return:
        """
        # check if passed alpha is ok
        _validate_alpha(alpha)

        # create the mtable
        mtable = None
        fc = AlphaAdjustment(k, self.__minProp, self.__alpha)
        return mtable

    def create_unadjusted_mtable(self):
        """
        Creates an mtable using alpha unadjusted
        :return:
        """
        pass

    def create_adjusted_mtable(self):
        """
        Creates an mtable using alpha adjusted
        :return:
        """
        pass

    def adjust_alpha(self):
        """
        Computes the alpha adjusted for the given set of parameters
        :return:
        """
        pass

    def compute_fail_probability(self):
        """
        Computes analytically the probability that a ranking created with the simulator will fail to pass the mtable
        :return:
        """

    def is_fair(self, ranking: list):
        """
        Checks if the ranking is fair for the given parameters
        :param ranking:     The ranking to be checked
        :return:
        """
        return check_ranking(ranking, self.create_adjusted_mtable())

def check_ranking(ranking:list, mtable: list):
    """
    Checks if the ranking is fair in respect to the mtable
    :param ranking:     The ranking to be checked
    :param mtable:      The mtable against to check
    :return:            Returns whether the rankings statisfies the mtable
    """
    count_protected = 0

    # if the mtable has a different number elements than there are in the top docs return false
    if len(ranking) != len(mtable):
        raise ValueError("Number of documents in ranking and mtable length are not the same!")

    # check number of protected element at each rank
    for i, element in ranking:
        count_protected += 1 if element == 1 else 0
        if count_protected < mtable[i]:
            return False
    return True

def _validate_basic_parameters(k, p, alpha):
    """
    Validates if k, p and alpha are in the required ranges
    :param k:           Total number of elements (above or equal to 10)
    :param p:           The proportion of protected candidates in the top-k ranking (between 0.02 and 0.98)
    :param alpha:       The significance level (between 0.01 and 0.15)
    """
    if k < 10:
        raise ValueError("Total number of elements `k` must be above or equal to 10")
    if p < 0.02 or p > 0.98:
        raise ValueError("The proportion of protected candidates `p` in the top-k ranking must between 0.02 and 0.98")

    _validate_alpha(alpha)

def _validate_alpha(alpha):
    if alpha < 0.01 or alpha > 0.15:
        raise ValueError("The significance level `alpha` must be between 0.01 and 0.15")

import random
from scipy.stats import binom

def generate_ranking(k, p):
    ''' Create a ranking of 'k' positions in which at each position the
        probability that the candidate is protected is 'p'.
    '''
    ranking = []
    for i in range(k):
        is_protected = (random.random() <= p)
        if is_protected:
            ranking.append(1)
        else:
            ranking.append(0)
    return ranking
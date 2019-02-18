from fairsearchcore import fair
from fairsearchcore import simulator

def not_test_is_fair():
    k = 20
    p = 0.25
    alpha = 0.1

    f = fair.Fair(k, p, alpha)

    rankings = simulator.generate_rankings(1, k, p)

    assert len(rankings) == 1

    assert f.is_fair(rankings[0])


def test_create_unadjusted_mtable():
    k = 20
    p = 0.25
    alpha = 0.1

    f = fair.Fair(k, p, alpha)

    mtable = f.create_unadjusted_mtable()

    assert isinstance(mtable, list)

    assert len(mtable) == 20

    print(mtable)
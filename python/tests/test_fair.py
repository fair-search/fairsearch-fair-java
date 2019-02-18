from fairsearchcore import fair
from fairsearchcore import simulator


def test_is_fair():
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

    assert mtable == [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3]


def test_create_adjusted_mtable():
    k = 20
    p = 0.25
    alpha = 0.1

    f = fair.Fair(k, p, alpha)

    mtable = f.create_adjusted_mtable()

    assert mtable == [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2]


def test_adjust_alpha():
    k = 20
    p = 0.25
    alpha = 0.1

    f = fair.Fair(k, p, alpha)

    adjusted_alpha = f.adjust_alpha()

    assert alpha != adjusted_alpha


def test_compute_fail_probability():
    k = 20
    p = 0.25
    alpha = 0.1

    f = fair.Fair(k, p, alpha)

    adjusted_mtable = f.create_adjusted_mtable()

    res = f.compute_fail_probability(adjusted_mtable)

    assert 0 < res
    assert res < 1


def test_compute_fail_probability_with_exact_numbers():
    k = 10
    p = 0.2
    alpha = 0.15

    f = fair.Fair(k, p, alpha)

    mtable = f.create_adjusted_mtable()

    print(mtable)

    res = f.compute_fail_probability(mtable)

    print(res)

    assert res == 0.1342177280000001

from fairsearchcore import  simulator

def test_fail_probability_calcualtors():
    Ms = [5000, 10000]
    ks = [10, 20, 50, 100, 200]
    ps = [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9]
    alphas = [0.01, 0.05, 0.1, 0.15]

    allowedOffset = 0.02 # we tolerate an absolute difference in probability of 0.02

    for M in Ms:
        for k in ks:
            for p in ps:
                for alpha in alphas:

                    rankings = simulator.generate_rankings(M, k, p)

                    print(rankings)
                    assert len(rankings) == M


"""
int[] Ms = {5000, 10000};
        int[] ks = {10, 20, 50, 100, 200};
        double[] ps = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
        double[] alphas = {0.01, 0.05, 0.1, 0.15};

        double allowedOffset = 0.02; // we tolerate an absolute difference in probability of 0.02

//        PrintWriter writer = new PrintWriter("D:\\tmp\\fair-tests-2.tsv");
//        writer.println(String.format("passed\tdifference\tk\tp\talpha\talpha_adjusted\tanalytical\texperimental\tM"));
        for(int M: Ms) {
            for(int k: ks) {
                for(double p : ps) {
                    for(double alpha : alphas) {
                        Fair fair = new Fair(k, p, alpha);
                        TopDocs[] rankings = Simulator.generateRankings(M, k, p);
                        double alpha_adujsted = fair.adjustAlpha();
                        int[] mtable = fair.createAdjustedMTable();
                        double experimental = Simulator.computeFailureProbability(mtable, rankings);
                        double analytical = fair.computeFailureProbability(mtable);
                        double actualOffset = Math.abs(analytical - experimental);
//                        if(actualErrorRate <= maximumErrorRate)
//                        writer.println(String.format("%b\t%.05f\t%d\t%.05f\t%.05f\t%.05f\t%.05f\t%.05f\t%d",
//                                actualOffset <= allowedOffset || (analytical == experimental), actualOffset,
//                                k, p, alpha, alpha_adujsted,
//                                analytical, experimental, M));
                        //add this just so the tests passes, but we need to see why it's failing
                        assertTrue(actualOffset <= allowedOffset);
                    }
                }
            }
        }
        """
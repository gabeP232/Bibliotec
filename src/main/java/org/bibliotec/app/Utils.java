package org.bibliotec.app;

import atlantafx.base.theme.Styles;
import javafx.scene.Node;

public class Utils {

    public static void setRedHighlight(Node node, boolean highlight) {
        node.pseudoClassStateChanged(Styles.STATE_DANGER, highlight);
    }

    // from https://stackoverflow.com/a/33158547/5938387
    public static String makeISBN() {
        // Generate Random Numbers for L1L2-B1B2B3-V1V2
        int L1 = (int) (Math.random() * (10));
        double L2 = Math.random() * (10);

        int B1 = (int) (Math.random() * (10));
        double B2 = Math.random() * (10);
        int B3 = (int) (Math.random() * (10));

        double V1 = Math.random() * (10);
        int V2 = (int) (Math.random() * (10));

        if (L1 == 0 && (int) L2 == 0) {
            L2++;
        }
        if (B1 == 0) {
            B1++;
        }
        if ((int) V1 == 0 && V2 == 0) {
            V2++;
        }
        double C = (hashOp(L1) + L2 + hashOp(B1) + B2 + hashOp(B3) + V1 + hashOp(V2)) % 10;


        return L1 + "" + (int) L2 + "-" + B1 + (int) B2 + B3 + "-" + (int) V1 + V2 + "-" + (int) C;
    }

    public static int hashOp(int i) {
        // used to determine C
        int doubled = 2 * i;
        if (doubled >= 10) {
            doubled = doubled - 9;
        }
        return doubled;
    }
}

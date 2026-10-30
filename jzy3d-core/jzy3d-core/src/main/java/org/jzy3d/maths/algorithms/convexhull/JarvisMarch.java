package org.jzy3d.maths.algorithms.convexhull;

import java.util.ArrayDeque;
import java.util.Deque;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.algorithms.convexhull.algorithms.XYComparator;
import org.jzy3d.maths.algorithms.convexhull.utils.QuickSort;

/**
 * Luokka kääntyy, muttei toimi vielä. Edit: Lista on muutettu taulukoksi.
 *
 * @author Ossi Lehto
 */
public class JarvisMarch implements ConvexHullFunction {


  /**
   * Palauttaa pinossa peitteen pisteet niin, että ensimmäinen ja viimeinen alkio ovat samat.
   *
   *
   * .@pre {Q.length > 1}
   */
  @Override
  public Deque<Coord2d> getConvexHull(Coord2d[] Q) {
    QuickSort.<Coord2d>sort(Q, new XYComparator());
    int lowest = 0;
    int highest = Q.length - 1;
    int n = 0;
    Deque<Integer> collinear = new ArrayDeque<Integer>();
    boolean pass = true;

    // Palautettava pino.
    Deque<Coord2d> S = new ArrayDeque<Coord2d>();
    S.push(Q[0]);

    /**
     * Oikea ketju *
     */
    while (n < highest) {
      n++;
      /* Kokeillaan pistettä n seuraavaksi palautettavaksi pisteeksi */
      for (int i = n + 1; i <= Q.length - 1; i++) {
        // löytyi piste, joka oli viivan väärällä puolella
        if (crossProduct(S.peek(), Q[n], Q[i]) < 0) {
          pass = false;
          break; // lopettaa ristitulojen tarkastamisen, pisteen vaihto
        }
        // löytyi kollineaarinen piste
        if (crossProduct(S.peek(), Q[n], Q[i]) == 0) {
          collinear.push(i);
        }
      } // for

      // seuraava piste löydetty
      if (pass) {

        // Kollienaarisista pisteistä otetaan viimeiseksi lisätty,
        // koska se on kauimmainen
        if (!collinear.isEmpty()) {
          n = collinear.pop();
        }

        S.push(Q[n]);
      } // if
      // alustetaan tarkistus
      while (!collinear.isEmpty()) {
        collinear.pop();
      }
      pass = true;

    } // while

    /**
     * Vasen ketju, tässä vaiheessa n==highest *
     */
    while (n > lowest) {
      n--;
      // Kokeillaan pistettä n seuraavaksi palautettavaksi pisteeksi
      for (int i = n - 1; i >= 0; i--) {
        // löytyi piste, joka oli viivan väärällä puolella
        if (crossProduct(Q[n], S.peek(), Q[i]) > 0) {
          pass = false;
          // lopettaa ristitulojen tarkastamisen, pisteen vaihto
          break;
        }

        // löytyi kollineaarinen piste
        if (crossProduct(Q[n], S.peek(), Q[i]) == 0) {
          collinear.push(i);
        }

      } // for

      // seuraava piste löydetty
      if (pass) {

        // Kollienaarisista pisteistä otetaan viimeiseksi lisätty,
        // koska se on kauimmainen
        if (!collinear.isEmpty()) {
          n = collinear.pop();
        }

        S.push(Q[n]);
      } // if
      // alustetaan tarkistus
      while (!collinear.isEmpty()) {
        collinear.pop();
      }
      pass = true;

    } // while

    return S;
  }


  /**
   * @.pre {true}
   */
  public JarvisMarch() {}

  /**
   * Laskee kolmion p1p2p3 alan
   */
  private double crossProduct(Coord2d p1, Coord2d p2, Coord2d p3) {
    return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY())
        - (p3.getX() - p1.getX()) * (p2.getY() - p1.getY());
  }
} // end of class

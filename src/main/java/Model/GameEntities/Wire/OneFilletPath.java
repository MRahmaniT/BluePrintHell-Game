package Model.GameEntities.Wire;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class OneFilletPath implements WirePath {
    private final Point2D.Float A, M, B;
    private final float r;

    private final boolean hasArc;
    private final Point2D.Float T1, T2, C;
    private final float thetaStart;     // angle of C->T1
    private final float sweep;          // signed sweep (CCW > 0, CW < 0)
    private final float len1, lenArc, len2, totalLen;

    public OneFilletPath(Point2D.Float A, Point2D.Float M, Point2D.Float B, float radius) {
        this.A = A; this.M = M; this.B = B; this.r = Math.max(0f, radius);

        // unit directions along the two segments AWAY from M
        Point2D.Float v1 = norm(vec(M, A));
        Point2D.Float v2 = norm(vec(M, B));
        float dot = clamp(v1.x*v2.x + v1.y*v2.y, -1f, 1f);
        float corner = (float) Math.acos(dot);    // 0..π

        // if almost straight, or tiny radius, fall back to straight
        if (corner < 1e-3f || r <= 1e-3f) {
            hasArc = false; T1 = T2 = C = null; thetaStart = 0f; sweep = 0f;
            len1 = (float) A.distance(B); lenArc = len2 = 0f; totalLen = len1;
            return;
        }

        // distance from M to tangent points along each segment
        float tLen = r * (float) (1.0 / Math.tan(corner / 2.0));
        if (tLen > (float) A.distance(M) - 1e-3f || tLen > (float) B.distance(M) - 1e-3f) {
            // radius too big for available leg lengths
            hasArc = false; T1 = T2 = C = null; thetaStart = 0f; sweep = 0f;
            len1 = (float) A.distance(B); lenArc = len2 = 0f; totalLen = len1;
            return;
        }

        // tangent points on the legs (from M towards A/B)
        Point2D.Float t1 = new Point2D.Float(M.x + v1.x * tLen, M.y + v1.y * tLen);
        Point2D.Float t2 = new Point2D.Float(M.x + v2.x * tLen, M.y + v2.y * tLen);

        // angle bisector (inside the corner)
        Point2D.Float bis = norm(new Point2D.Float(v1.x + v2.x, v1.y + v2.y));
        float dCenter = r / (float) Math.sin(corner / 2.0);
        Point2D.Float c = new Point2D.Float(M.x + bis.x * dCenter, M.y + bis.y * dCenter);

        // start angle at T1
        float thStart = (float) Math.atan2(t1.y - c.y, t1.x - c.x);

        // orientation: +CCW / -CW from v1 to v2 at the corner
        float cross = v1.x * v2.y - v1.y * v2.x;
        float signedSweep = (cross >= 0f ? +corner : -corner);

        hasArc = true;
        T1 = t1; T2 = t2; C = c;
        thetaStart = thStart;
        sweep = signedSweep;

        len1 = (float) A.distance(T1);
        lenArc = r * Math.abs(sweep);
        len2 = (float) T2.distance(B);
        totalLen = len1 + lenArc + len2;
    }

    @Override public float length() { return totalLen; }

    @Override
    public Path2D.Float getPath() {
        return null;
    }

    @Override public Point2D.Float pointAt(float t) {
        t = clamp(t, 0f, 1f);
        if (!hasArc) return lerp(A, B, t);
        float L = t * totalLen;
        if (L <= len1) return lerp(A, T1, safeDiv(L, len1));

        if (L <= len1 + lenArc) {
            float u = safeDiv(L - len1, lenArc);       // 0..1 along the arc
            float th = thetaStart + u * sweep;         // signed sweep
            return new Point2D.Float(C.x + r * (float)Math.cos(th),
                    C.y + r * (float)Math.sin(th));
        }

        float rem = L - (len1 + lenArc);
        return lerp(T2, B, safeDiv(rem, len2));
    }

    @Override public Point2D.Float tangentAt(float t) {
        t = clamp(t, 0f, 1f);
        if (!hasArc) return norm(vec(A, B));

        float L = t * totalLen;
        if (L <= len1) return norm(vec(A, T1));

        if (L <= len1 + lenArc) {
            float u = safeDiv(L - len1, lenArc);
            float th = thetaStart + u * sweep;
            // derivative of circle param (CCW positive, CW negative)
            float sgn = (sweep >= 0f ? 1f : -1f);
            return new Point2D.Float(-sgn * (float)Math.sin(th),
                    sgn * (float)Math.cos(th));
        }

        return norm(vec(T2, B));
    }

    @Override public Nearest nearestTo(Point2D.Float P) {
        if (!hasArc) {
            float tt = projectParam(A, B, P);
            Point2D.Float Q = lerp(A, B, tt);
            return new Nearest(Q, tt, (float) Q.distance(P));
        }

        SegNearest n1 = nearestOnSegment(A, T1, P);
        float t1 = (len1 == 0f) ? 0f : (n1.t * (len1 / totalLen));

        // clamp angle onto the directed arc
        float ang = (float) Math.atan2(P.y - C.y, P.x - C.x);
        float th = clampToDirectedArc(ang, thetaStart, sweep);
        Point2D.Float Qa = new Point2D.Float(C.x + r * (float)Math.cos(th),
                C.y + r * (float)Math.sin(th));
        float arcLenAt = Math.abs(th - thetaStart) * r;
        float ta = (len1 + arcLenAt) / totalLen;

        SegNearest n3 = nearestOnSegment(T2, B, P);
        float tb = (len1 + lenArc + n3.t * len2) / totalLen;

        float d1 = (float) n1.Q.distance(P);
        float d2 = (float) Qa.distance(P);
        float d3 = (float) n3.Q.distance(P);

        if (d1 <= d2 && d1 <= d3) return new Nearest(n1.Q, t1, d1);
        if (d2 <= d3)             return new Nearest(Qa,  ta, d2);
        return new Nearest(n3.Q, tb, d3);
    }

    @Override public Shape toShape() {
        Path2D.Float path = new Path2D.Float();
        if (!hasArc) {
            path.moveTo(A.x, A.y);
            path.lineTo(B.x, B.y);
            return path;
        }

        path.moveTo(A.x, A.y);
        path.lineTo(T1.x, T1.y);

        double startDeg  = Math.toDegrees(thetaStart);
        double extentDeg = Math.toDegrees(sweep);   // keep sign: +CCW, -CW

        double x = C.x - r, y = C.y - r, d = r * 2.0;
        Arc2D.Double arc = new Arc2D.Double(x, y, d, d, startDeg, extentDeg, Arc2D.OPEN);

        // connect seamlessly to arc start and continue through to its end
        //path.append(arc, true);

        path.lineTo(B.x, B.y);
        return path;
    }

    /* ---------- helpers ---------- */
    private static Point2D.Float vec(Point2D.Float P, Point2D.Float Q) {
        return new Point2D.Float(Q.x - P.x, Q.y - P.y);
    }
    private static Point2D.Float lerp(Point2D.Float P, Point2D.Float Q, float t) {
        t = clamp(t, 0f, 1f);
        return new Point2D.Float(P.x + (Q.x - P.x) * t, P.y + (Q.y - P.y) * t);
    }
    private static Point2D.Float norm(Point2D.Float v) {
        float L = (float) Math.hypot(v.x, v.y);
        return (L < 1e-8f) ? new Point2D.Float(0,0) : new Point2D.Float(v.x/L, v.y/L);
    }
    private static float clamp(float v, float a, float b) { return Math.max(a, Math.min(b, v)); }
    private static float safeDiv(float num, float den) { return (den < 1e-8f) ? 0f : num/den; }
    private static float projectParam(Point2D.Float A, Point2D.Float B, Point2D.Float P) {
        float dx = B.x - A.x, dy = B.y - A.y;
        float L2 = dx*dx + dy*dy;
        if (L2 < 1e-8f) return 0f;
        float t = ((P.x - A.x)*dx + (P.y - A.y)*dy) / L2;
        return clamp(t, 0f, 1f);
    }
    private record SegNearest(Point2D.Float Q, float t) {}
    private static SegNearest nearestOnSegment(Point2D.Float A, Point2D.Float B, Point2D.Float P) {
        float t = projectParam(A, B, P);
        return new SegNearest(new Point2D.Float(A.x + (B.x - A.x)*t, A.y + (B.y - A.y)*t), t);
    }
    private static float clampToDirectedArc(float ang, float start, float sweep) {
        // map ang into the directed range [start, start+sweep]
        float end = start + sweep;
        // normalize to (-π, π] around start
        float a = ang - start;
        while (a <= -Math.PI) a += 2*Math.PI;
        while (a >   Math.PI) a -= 2*Math.PI;
        if (sweep >= 0f) {
            if (a < 0f) return start;
            if (a > sweep) return end;
            return ang;
        } else {
            if (a > 0f) return start;
            if (a < sweep) return end;
            return ang;
        }
    }
}

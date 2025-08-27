package Model.GameEntities.Wire;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class OneFilletPath implements WirePath {

    private final Point2D.Float A, M, B;
    private final float r;

    // Piecewise breakpoints
    private final boolean hasArc;
    private final Point2D.Float T1, T2;      // tangent points on first/second legs
    private final Point2D.Float C;           // circle center
    private final float thetaStart, thetaEnd; // arc angles (CCW)
    private final float len1, lenArc, len2, totalLen;

    public OneFilletPath(Point2D.Float A, Point2D.Float M, Point2D.Float B, float radius) {
        this.A = A; this.M = M; this.B = B; this.r = Math.max(0f, radius);

        // vectors of legs (pointing away from corner M)
        Point2D.Float v1 = norm(vec(M, A)); // from M toward A
        Point2D.Float v2 = norm(vec(M, B)); // from M toward B
        // If legs are collinear or degenerate, fallback to straight line path
        float dot = v1.x * v2.x + v1.y * v2.y;
        dot = clamp(dot, -1f, 1f);
        float angle = (float)Math.acos(dot); // angle between legs

        // If angle is ~0 or ~pi, no corner;
        if (angle < 1e-3f || Math.abs(angle - (float)Math.PI) < 1e-3f || r <= 1e-3f) {
            this.hasArc = false;
            this.T1 = null; this.T2 = null; this.C = null;
            this.thetaStart = 0; this.thetaEnd = 0;
            this.len1 = (float)A.distance(B);
            this.lenArc = 0f;
            this.len2 = 0f;
            this.totalLen = len1;
            return;
        }

        // distance from corner along each leg to tangent point
        // tLen = r * cot(angle/2)
        float tLen = r * (float)(1.0 / Math.tan(angle / 2.0));

        // candidate tangent points
        Point2D.Float t1cand = new Point2D.Float(M.x + v1.x * tLen, M.y + v1.y * tLen);
        Point2D.Float t2cand = new Point2D.Float(M.x + v2.x * tLen, M.y + v2.y * tLen);

        // Make sure tangent points are *on* the legs (i.e., between A–M and M–B)
        if (tLen > (float)A.distance(M) - 1e-3f || tLen > (float)B.distance(M) - 1e-3f) {
            // radius too big to fit; fallback
            this.hasArc = false;
            this.T1 = null; this.T2 = null; this.C = null;
            this.thetaStart = 0; this.thetaEnd = 0;
            this.len1 = (float)A.distance(B);
            this.lenArc = 0f;
            this.len2 = 0f;
            this.totalLen = len1;
            return;
        }

        // arc center lies on the angle bisector from M, at distance r / sin(angle/2)
        Point2D.Float bis = norm(new Point2D.Float(v1.x + v2.x, v1.y + v2.y));
        float dCenter = r / (float)Math.sin(angle / 2.0);
        Point2D.Float Ccand = new Point2D.Float(M.x + bis.x * dCenter, M.y + bis.y * dCenter);

        // compute arc start/end angles (T1->T2 along the smaller arc)
        float th1 = (float)Math.atan2(t1cand.y - Ccand.y, t1cand.x - Ccand.x);
        float th2 = (float)Math.atan2(t2cand.y - Ccand.y, t2cand.x - Ccand.x);

        // choose CCW sweep that follows from leg1 to leg2 turning via inside of corner:
        float sweep = angle;                   // arc central angle equals the corner angle
        float thStart = th1;
        float thEnd   = angleCCW(th1, th1 + sweep);

        // Ensure thEnd corresponds to t2cand (wrap if needed)
        float alt = angleCCW(th1, th2);
        if (Math.abs(alt - sweep) > 1e-2) {
            // flip direction if our guess is off
            thEnd = angleCW(th1, th1 - sweep);
        }

        this.hasArc = true;
        this.T1 = t1cand;
        this.T2 = t2cand;
        this.C  = Ccand;
        this.thetaStart = thStart;
        this.thetaEnd   = thEnd;

        this.len1 = (float)A.distance(T1);
        this.lenArc = r * sweep;
        this.len2 = (float)T2.distance(B);
        this.totalLen = len1 + lenArc + len2;
    }

    @Override
    public float length() { return totalLen; }

    @Override
    public Shape toShape() {
        Path2D.Float path = new Path2D.Float();
        if (!hasArc) {
            path.moveTo(A.x, A.y);
            path.lineTo(B.x, B.y);
            return path;
        }

        path.moveTo(A.x, A.y);
        path.lineTo(T1.x, T1.y);

        double startDeg  = Math.toDegrees(thetaStart);
        double extentDeg = Math.toDegrees(thetaEnd - thetaStart); // CCW sweep
        // Ensure positive CCW extent
        if (extentDeg < 0) extentDeg += 360.0;

        double x = C.x - r, y = C.y - r, d = r * 2.0;
        Arc2D.Double arc = new Arc2D.Double(T1.x, T2.y, d, d, startDeg, extentDeg, Arc2D.OPEN);
        path.append(arc, true);

        path.lineTo(B.x, B.y);
        return path;
    }

    @Override
    public Path2D.Float getPath() {
        return null;
    }

    @Override
    public Point2D.Float pointAt(float t) {
        t = clamp(t, 0f, 1f);
        if (!hasArc) {
            return lerp(A, B, t);
        }
        float L = t * totalLen;
        if (L <= len1) {
            float u = (len1 == 0f) ? 0f : (L / len1);
            return lerp(A, T1, u);
        } else if (L <= len1 + lenArc) {
            float u = (L - len1) / lenArc; // 0..1 along arc
            float th = lerpAngle(thetaStart, thetaEnd, u);
            return new Point2D.Float(C.x + r * (float)Math.cos(th),
                    C.y + r * (float)Math.sin(th));
        } else {
            float rem = L - (len1 + lenArc);
            float u = (len2 == 0f) ? 0f : (rem / len2);
            return lerp(T2, B, u);
        }
    }

    @Override
    public Point2D.Float tangentAt(float t) {
        t = clamp(t, 0f, 1f);
        if (!hasArc) {
            return norm(vec(A, B));
        }
        float L = t * totalLen;
        if (L <= len1) {
            return norm(vec(A, T1));
        } else if (L <= len1 + lenArc) {
            float u = (L - len1) / lenArc;
            float th = lerpAngle(thetaStart, thetaEnd, u);
            // tangent is perpendicular to radius, CCW
            return new Point2D.Float(-(float)Math.sin(th), (float)Math.cos(th));
        } else {
            return norm(vec(T2, B));
        }
    }

    @Override
    public Nearest nearestTo(Point2D.Float P) {
        if (!hasArc) {
            // nearest to straight A-B
            float t = projectParam(A, B, P);
            Point2D.Float Q = lerp(A, B, t);
            return new Nearest(t, Q, (float)Q.distance(P));
        }

        // 1) segment A-T1
        SegNearest na = nearestOnSegment(A, T1, P);
        float t1 = na.t * (len1 / totalLen); // map to [0..len1] → [0..(len1/totalLen)]

        // 2) arc T1→T2
        // nearest on circle is radial projection clamped to arc angles
        float ang = (float)Math.atan2(P.y - C.y, P.x - C.x);
        float th = clampAngleToSpan(ang, thetaStart, thetaEnd); // clamp to arc span (CCW)
        Point2D.Float Qa = new Point2D.Float(C.x + r * (float)Math.cos(th),
                C.y + r * (float)Math.sin(th));
        float arcLenAt = arcLengthBetween(thetaStart, th, r); // 0..lenArc
        float ta = (len1 + arcLenAt) / totalLen;              // map to global t

        // 3) segment T2-B
        SegNearest nb = nearestOnSegment(T2, B, P);
        float tb = (len1 + lenArc + nb.t * len2) / totalLen;

        // choose best
        Point2D.Float Qa1 = na.Q;
        float da1 = (float)Qa1.distance(P);
        float da2 = (float)Qa.distance(P);
        Point2D.Float Qa3 = nb.Q;
        float da3 = (float)Qa3.distance(P);

        if (da1 <= da2 && da1 <= da3) return new Nearest(t1, Qa1, da1);
        if (da2 <= da3)               return new Nearest(ta,  Qa, da2);
        return new Nearest(tb, Qa3, da3);
    }

    /* ---------------- helpers ---------------- */

    private static Point2D.Float vec(Point2D.Float P, Point2D.Float Q) {
        return new Point2D.Float(Q.x - P.x, Q.y - P.y);
    }
    private static Point2D.Float norm(Point2D.Float v) {
        float L = (float)Math.hypot(v.x, v.y);
        if (L < 1e-8f) return new Point2D.Float(0, 0);
        return new Point2D.Float(v.x / L, v.y / L);
    }
    private static float clamp(float v, float a, float b) {
        return Math.max(a, Math.min(b, v));
    }
    private static Point2D.Float lerp(Point2D.Float P, Point2D.Float Q, float t) {
        t = clamp(t, 0f, 1f);
        return new Point2D.Float(P.x + (Q.x - P.x) * t, P.y + (Q.y - P.y) * t);
    }

    // project param of P onto segment AB in [0..1]
    private static float projectParam(Point2D.Float A, Point2D.Float B, Point2D.Float P) {
        float dx = B.x - A.x, dy = B.y - A.y;
        float L2 = dx*dx + dy*dy;
        if (L2 < 1e-8f) return 0f;
        float t = ((P.x - A.x)*dx + (P.y - A.y)*dy) / L2;
        return clamp(t, 0f, 1f);
    }
    private static class SegNearest {
        final Point2D.Float Q; final float t;
        SegNearest(Point2D.Float Q, float t) { this.Q = Q; this.t = t; }
    }
    private static SegNearest nearestOnSegment(Point2D.Float A, Point2D.Float B, Point2D.Float P) {
        float t = projectParam(A, B, P);
        return new SegNearest(lerp(A, B, t), t);
    }

    // angles helpers
    private static float angleCCW(float from, float to) {
        float a = (to - from) % (float)(2*Math.PI);
        if (a < 0) a += 2*(float)Math.PI;
        return from + a;
    }
    private static float angleCW(float from, float to) {
        float a = (to - from) % (float)(2*Math.PI);
        if (a > 0) a -= 2*(float)Math.PI;
        return from + a;
    }
    private static float lerpAngle(float a0, float a1, float t) {
        // go the shorter way CCW from a0 to a1
        float d = (a1 - a0) % (float)(2*Math.PI);
        if (d > Math.PI) d -= 2*(float)Math.PI;
        if (d < -Math.PI) d += 2*(float)Math.PI;
        return a0 + d * clamp(t, 0f, 1f);
    }
    private static float clampAngleToSpan(float ang, float a0, float a1) {
        // clamp 'ang' to CCW span [a0..a1]
        float a = (ang - a0) % (float)(2*Math.PI);
        if (a < 0) a += 2*(float)Math.PI;
        float span = (a1 - a0) % (float)(2*Math.PI);
        if (span < 0) span += 2*(float)Math.PI;
        if (a <= span) return ang;              // already inside
        return a0 + span;                       // clamp to end
    }
    private static float arcLengthBetween(float th0, float th1, float r) {
        float d = (th1 - th0) % (float)(2*Math.PI);
        if (d < 0) d += 2*(float)Math.PI;
        return d * r;
    }
}

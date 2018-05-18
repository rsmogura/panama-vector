/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have
 * questions.
 */
package jdk.incubator.vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Objects;
import jdk.internal.vm.annotation.ForceInline;
import static jdk.incubator.vector.VectorIntrinsics.*;

@SuppressWarnings("cast")
final class Float256Vector extends FloatVector<Shapes.S256Bit> {
    static final Float256Species SPECIES = new Float256Species();

    static final Float256Vector ZERO = new Float256Vector();

    static final int LENGTH = SPECIES.length();

    private final float[] vec; // Don't access directly, use getElements() instead.

    private float[] getElements() {
        return VectorIntrinsics.maybeRebox(this).vec;
    }

    Float256Vector() {
        vec = new float[SPECIES.length()];
    }

    Float256Vector(float[] v) {
        vec = v;
    }

    @Override
    public int length() { return LENGTH; }

    // Unary operator

    @Override
    Float256Vector uOp(FUnOp f) {
        float[] vec = getElements();
        float[] res = new float[length()];
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec[i]);
        }
        return new Float256Vector(res);
    }

    @Override
    Float256Vector uOp(Mask<Float, Shapes.S256Bit> o, FUnOp f) {
        float[] vec = getElements();
        float[] res = new float[length()];
        boolean[] mbits = ((Float256Mask)o).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec[i]) : vec[i];
        }
        return new Float256Vector(res);
    }

    // Binary operator

    @Override
    Float256Vector bOp(Vector<Float, Shapes.S256Bit> o, FBinOp f) {
        float[] res = new float[length()];
        float[] vec1 = this.getElements();
        float[] vec2 = ((Float256Vector)o).getElements();
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec1[i], vec2[i]);
        }
        return new Float256Vector(res);
    }

    @Override
    Float256Vector bOp(Vector<Float, Shapes.S256Bit> o1, Mask<Float, Shapes.S256Bit> o2, FBinOp f) {
        float[] res = new float[length()];
        float[] vec1 = this.getElements();
        float[] vec2 = ((Float256Vector)o1).getElements();
        boolean[] mbits = ((Float256Mask)o2).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec1[i], vec2[i]) : vec1[i];
        }
        return new Float256Vector(res);
    }

    // Trinary operator

    @Override
    Float256Vector tOp(Vector<Float, Shapes.S256Bit> o1, Vector<Float, Shapes.S256Bit> o2, FTriOp f) {
        float[] res = new float[length()];
        float[] vec1 = this.getElements();
        float[] vec2 = ((Float256Vector)o1).getElements();
        float[] vec3 = ((Float256Vector)o2).getElements();
        for (int i = 0; i < length(); i++) {
            res[i] = f.apply(i, vec1[i], vec2[i], vec3[i]);
        }
        return new Float256Vector(res);
    }

    @Override
    Float256Vector tOp(Vector<Float, Shapes.S256Bit> o1, Vector<Float, Shapes.S256Bit> o2, Mask<Float, Shapes.S256Bit> o3, FTriOp f) {
        float[] res = new float[length()];
        float[] vec1 = getElements();
        float[] vec2 = ((Float256Vector)o1).getElements();
        float[] vec3 = ((Float256Vector)o2).getElements();
        boolean[] mbits = ((Float256Mask)o3).getBits();
        for (int i = 0; i < length(); i++) {
            res[i] = mbits[i] ? f.apply(i, vec1[i], vec2[i], vec3[i]) : vec1[i];
        }
        return new Float256Vector(res);
    }

    @Override
    float rOp(float v, FBinOp f) {
        float[] vec = getElements();
        for (int i = 0; i < length(); i++) {
            v = f.apply(i, v, vec[i]);
        }
        return v;
    }

    // Binary operations with scalars

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> add(float o) {
        return add(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> add(float o, Mask<Float,Shapes.S256Bit> m) {
        return add(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> sub(float o) {
        return sub(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> sub(float o, Mask<Float,Shapes.S256Bit> m) {
        return sub(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> mul(float o) {
        return mul(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> mul(float o, Mask<Float,Shapes.S256Bit> m) {
        return mul(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> min(float o) {
        return min(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> max(float o) {
        return max(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Float, Shapes.S256Bit> equal(float o) {
        return equal(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Float, Shapes.S256Bit> notEqual(float o) {
        return notEqual(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Float, Shapes.S256Bit> lessThan(float o) {
        return lessThan(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Float, Shapes.S256Bit> lessThanEq(float o) {
        return lessThanEq(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Float, Shapes.S256Bit> greaterThan(float o) {
        return greaterThan(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public Mask<Float, Shapes.S256Bit> greaterThanEq(float o) {
        return greaterThanEq(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> blend(float o, Mask<Float,Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> div(float o) {
        return div(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> div(float o, Mask<Float,Shapes.S256Bit> m) {
        return div(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> atan2(float o) {
        return atan2(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> atan2(float o, Mask<Float,Shapes.S256Bit> m) {
        return atan2(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> pow(float o) {
        return pow(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> pow(float o, Mask<Float,Shapes.S256Bit> m) {
        return pow(SPECIES.broadcast(o), m);
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> fma(float o1, float o2) {
        return fma(SPECIES.broadcast(o1), SPECIES.broadcast(o2));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> fma(float o1, float o2, Mask<Float,Shapes.S256Bit> m) {
        return fma(SPECIES.broadcast(o1), SPECIES.broadcast(o2), m);
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> hypot(float o) {
        return hypot(SPECIES.broadcast(o));
    }

    @Override
    @ForceInline
    public FloatVector<Shapes.S256Bit> hypot(float o, Mask<Float,Shapes.S256Bit> m) {
        return hypot(SPECIES.broadcast(o), m);
    }


    // Unary operations

    @Override
    @ForceInline
    public Float256Vector abs() {
        return (Float256Vector) VectorIntrinsics.unaryOp(
            VECTOR_OP_ABS, Float256Vector.class, float.class, LENGTH,
            this,
            v1 -> ((Float256Vector)v1).uOp((i, a) -> (float) Math.abs(a)));
    }

    @Override
    @ForceInline
    public Float256Vector neg() {
        return (Float256Vector) VectorIntrinsics.unaryOp(
            VECTOR_OP_NEG, Float256Vector.class, float.class, LENGTH,
            this,
            v1 -> ((Float256Vector)v1).uOp((i, a) -> (float) -a));
    }

    @Override
    @ForceInline
    public Float256Vector div(Vector<Float,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;
        return (Float256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_DIV, Float256Vector.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> ((Float256Vector)v1).bOp(v2, (i, a, b) -> (float)(a / b)));
    }

    @Override
    @ForceInline
    public Float256Vector sqrt() {
        return (Float256Vector) VectorIntrinsics.unaryOp(
            VECTOR_OP_SQRT, Float256Vector.class, float.class, LENGTH,
            this,
            v1 -> ((Float256Vector)v1).uOp((i, a) -> (float) Math.sqrt((double) a)));
    }

    // Binary operations

    @Override
    @ForceInline
    public Float256Vector add(Vector<Float,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;
        return (Float256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_ADD, Float256Vector.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> ((Float256Vector)v1).bOp(v2, (i, a, b) -> (float)(a + b)));
    }

    @Override
    @ForceInline
    public Float256Vector sub(Vector<Float,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;
        return (Float256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_SUB, Float256Vector.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> ((Float256Vector)v1).bOp(v2, (i, a, b) -> (float)(a - b)));
    }

    @Override
    @ForceInline
    public Float256Vector mul(Vector<Float,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;
        return (Float256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_MUL, Float256Vector.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> ((Float256Vector)v1).bOp(v2, (i, a, b) -> (float)(a * b)));
    }

    @Override
    @ForceInline
    public Float256Vector min(Vector<Float,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;
        return (Float256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_MIN, Float256Vector.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> ((Float256Vector)v1).bOp(v2, (i, a, b) -> (float) ((a < b) ? a : b)));
    }

    @Override
    @ForceInline
    public Float256Vector max(Vector<Float,Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;
        return (Float256Vector) VectorIntrinsics.binaryOp(
            VECTOR_OP_MAX, Float256Vector.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> ((Float256Vector)v1).bOp(v2, (i, a, b) -> (float) ((a > b) ? a : b)));
        }

    @Override
    @ForceInline
    public Float256Vector add(Vector<Float,Shapes.S256Bit> v, Mask<Float, Shapes.S256Bit> m) {
        // TODO: use better default impl: bOp(o, m, (i, a, b) -> (float)(a + b));
        return blend(add(v), m);
    }

    @Override
    @ForceInline
    public Float256Vector sub(Vector<Float,Shapes.S256Bit> v, Mask<Float, Shapes.S256Bit> m) {
        // TODO: use better default impl: bOp(o, m, (i, a, b) -> (float)(a - b));
        return blend(sub(v), m);
    }

    @Override
    @ForceInline
    public Float256Vector mul(Vector<Float,Shapes.S256Bit> v, Mask<Float, Shapes.S256Bit> m) {
        // TODO: use better default impl: bOp(o, m, (i, a, b) -> (float)(a * b));
        return blend(mul(v), m);
    }

    @Override
    @ForceInline
    public Float256Vector div(Vector<Float,Shapes.S256Bit> v, Mask<Float, Shapes.S256Bit> m) {
        // TODO: use better default impl: bOp(o, m, (i, a, b) -> (float)(a / b));
        return blend(div(v), m);
    }


    // Ternary operations

    @Override
    @ForceInline
    public Float256Vector fma(Vector<Float,Shapes.S256Bit> o1, Vector<Float,Shapes.S256Bit> o2) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);
        Float256Vector v1 = (Float256Vector)o1;
        Float256Vector v2 = (Float256Vector)o2;
        return (Float256Vector) VectorIntrinsics.ternaryOp(
            VECTOR_OP_FMA, Float256Vector.class, float.class, LENGTH,
            this, v1, v2,
            (w1, w2, w3) -> w1.tOp(w2, w3, (i, a, b, c) -> Math.fma(a, b, c)));
    }

    // Type specific horizontal reductions

    @Override
    @ForceInline
    public float addAll() {
        int bits = (int) VectorIntrinsics.reductionCoerced(
                                VECTOR_OP_ADD, Float256Vector.class, float.class, LENGTH,
                                this,
                                v -> {
                                    float r = v.rOp((float) 0, (i, a, b) -> (float) (a + b));
                                    return (long)Float.floatToIntBits(r);
                                });
        return Float.intBitsToFloat(bits);
    }

    @Override
    @ForceInline
    public float subAll() {
        int bits = (int) VectorIntrinsics.reductionCoerced(
                                VECTOR_OP_SUB, Float256Vector.class, float.class, LENGTH,
                                this,
                                v -> {
                                    float r = v.rOp((float) 0, (i, a, b) -> (float) (a - b));
                                    return (long)Float.floatToIntBits(r);
                                });
        return Float.intBitsToFloat(bits);
    }

    @Override
    @ForceInline
    public float mulAll() {
        int bits = (int) VectorIntrinsics.reductionCoerced(
                                VECTOR_OP_MUL, Float256Vector.class, float.class, LENGTH,
                                this,
                                v -> {
                                    float r = v.rOp((float) 1, (i, a, b) -> (float) (a * b));
                                    return (long)Float.floatToIntBits(r);
                                });
        return Float.intBitsToFloat(bits);
    }


    @Override
    @ForceInline
    public float addAll(Mask<Float, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((float) 0), m).addAll();
    }

    @Override
    @ForceInline
    public float subAll(Mask<Float, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((float) 0), m).subAll();
    }

    @Override
    @ForceInline
    public float mulAll(Mask<Float, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast((float) 1), m).mulAll();
    }

    @Override
    @ForceInline
    public float minAll(Mask<Float, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(Float.MAX_VALUE), m).minAll();
    }

    @Override
    @ForceInline
    public float maxAll(Mask<Float, Shapes.S256Bit> m) {
        return blend(SPECIES.broadcast(Float.MIN_VALUE), m).maxAll();
    }

    // Memory operations

    @Override
    @ForceInline
    public void intoArray(float[] a, int ix) {
        Objects.requireNonNull(a);
        ix = VectorIntrinsics.checkIndex(ix, a.length, LENGTH);
        VectorIntrinsics.store(Float256Vector.class, float.class, LENGTH,
                               a, ix, this,
                               (arr, idx, v) -> v.forEach((i, a_) -> ((float[])arr)[idx + i] = a_));
    }

    @Override
    @ForceInline
    public void intoArray(float[] a, int ax, Mask<Float, Shapes.S256Bit> m) {
        // TODO: use better default impl: forEach(m, (i, a_) -> a[ax + i] = a_);
        Float256Vector oldVal = SPECIES.fromArray(a, ax);
        Float256Vector newVal = oldVal.blend(this, m);
        newVal.intoArray(a, ax);
    }

    @Override
    @ForceInline
    public void intoByteArray(byte[] a, int ix) {
        Objects.requireNonNull(a);
        ix = VectorIntrinsics.checkIndex(ix, a.length, bitSize() / Byte.SIZE);
        VectorIntrinsics.store(Float256Vector.class, float.class, LENGTH,
                               a, ix, this,
                               (arr, idx, v) -> {
                                   byte[] tarr = (byte[])arr;
                                   ByteBuffer bb = ByteBuffer.wrap(tarr, idx, tarr.length - idx).order(ByteOrder.nativeOrder());
                                   FloatBuffer fb = bb.asFloatBuffer();
                                   v.forEach((i, e) -> fb.put(e));
                               });
    }

    @Override
    @ForceInline
    public void intoByteArray(byte[] a, int ix, Mask<Float, Shapes.S256Bit> m) {
        Float256Vector oldVal = SPECIES.fromByteArray(a, ix);
        Float256Vector newVal = oldVal.blend(this, m);
        newVal.intoByteArray(a, ix);
    }

    @Override
    @ForceInline
    public void intoByteBuffer(ByteBuffer bb) {
        if (bb.hasArray() && !bb.isReadOnly() && bb.order() == ByteOrder.nativeOrder()) {
            int num_bytes = bitSize() / Byte.SIZE;
            int ix = VectorIntrinsics.checkIndex(bb.position(), bb.limit(), num_bytes);
            VectorIntrinsics.store(Float256Vector.class, float.class, LENGTH,
                                   bb.array(), ix, this,
                                   (arr, idx, v) -> {
                                       byte[] tarr = (byte[])arr;
                                       ByteBuffer lbb = ByteBuffer.wrap(tarr, idx, tarr.length - idx).order(ByteOrder.nativeOrder());
                                       FloatBuffer fb = lbb.asFloatBuffer();
                                       v.forEach((i, e) -> fb.put(e));
                                   });
        } else {
            super.intoByteBuffer(bb);
        }
    }

    @Override
    @ForceInline
    public void intoByteBuffer(ByteBuffer bb, Mask<Float, Shapes.S256Bit> m) {
        int idx = bb.position();
        Float256Vector oldVal = SPECIES.fromByteBuffer(bb, idx);
        Float256Vector newVal = oldVal.blend(this, m);
        newVal.intoByteBuffer(bb, idx);
    }

    @Override
    @ForceInline
    public void intoByteBuffer(ByteBuffer bb, int ix) {
        if (bb.hasArray() && !bb.isReadOnly() && bb.order() == ByteOrder.nativeOrder()) {
            int num_bytes = bitSize() / Byte.SIZE;
            int ax = VectorIntrinsics.checkIndex(ix, bb.limit(), num_bytes);
            VectorIntrinsics.store(Float256Vector.class, float.class, LENGTH,
                                   bb.array(), ax, this,
                                   (arr, idx, v) -> {
                                       byte[] tarr = (byte[])arr;
                                       ByteBuffer lbb = ByteBuffer.wrap(tarr, idx, tarr.length - idx).order(ByteOrder.nativeOrder());
                                       FloatBuffer fb = lbb.asFloatBuffer();
                                       v.forEach((i, e) -> fb.put(e));
                                   });
        } else {
            super.intoByteBuffer(bb, ix);
        }
    }

    @Override
    @ForceInline
    public void intoByteBuffer(ByteBuffer bb, int ix, Mask<Float, Shapes.S256Bit> m) {
        Float256Vector oldVal = SPECIES.fromByteBuffer(bb, ix);
        Float256Vector newVal = oldVal.blend(this, m);
        newVal.intoByteBuffer(bb, ix);
    }

    //

    @Override
    public String toString() {
        return Arrays.toString(getElements());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Float256Vector that = (Float256Vector) o;
        return Arrays.equals(this.getElements(), that.getElements());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vec);
    }

    // Binary test

    @Override
    Float256Mask bTest(Vector<Float, Shapes.S256Bit> o, FBinTest f) {
        float[] vec1 = getElements();
        float[] vec2 = ((Float256Vector)o).getElements();
        boolean[] bits = new boolean[length()];
        for (int i = 0; i < length(); i++){
            bits[i] = f.apply(i, vec1[i], vec2[i]);
        }
        return new Float256Mask(bits);
    }

    // Comparisons

    @Override
    @ForceInline
    public Float256Mask equal(Vector<Float, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;

        return (Float256Mask) VectorIntrinsics.compare(
            BT_eq, Float256Vector.class, Float256Mask.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a == b));
    }

    @Override
    @ForceInline
    public Float256Mask notEqual(Vector<Float, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;

        return (Float256Mask) VectorIntrinsics.compare(
            BT_ne, Float256Vector.class, Float256Mask.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a != b));
    }

    @Override
    @ForceInline
    public Float256Mask lessThan(Vector<Float, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;

        return (Float256Mask) VectorIntrinsics.compare(
            BT_lt, Float256Vector.class, Float256Mask.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a < b));
    }

    @Override
    @ForceInline
    public Float256Mask lessThanEq(Vector<Float, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;

        return (Float256Mask) VectorIntrinsics.compare(
            BT_le, Float256Vector.class, Float256Mask.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a <= b));
    }

    @Override
    @ForceInline
    public Float256Mask greaterThan(Vector<Float, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;

        return (Float256Mask) VectorIntrinsics.compare(
            BT_gt, Float256Vector.class, Float256Mask.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a > b));
    }

    @Override
    @ForceInline
    public Float256Mask greaterThanEq(Vector<Float, Shapes.S256Bit> o) {
        Objects.requireNonNull(o);
        Float256Vector v = (Float256Vector)o;

        return (Float256Mask) VectorIntrinsics.compare(
            BT_ge, Float256Vector.class, Float256Mask.class, float.class, LENGTH,
            this, v,
            (v1, v2) -> v1.bTest(v2, (i, a, b) -> a >= b));
    }

    // Foreach

    @Override
    void forEach(FUnCon f) {
        float[] vec = getElements();
        for (int i = 0; i < length(); i++) {
            f.apply(i, vec[i]);
        }
    }

    @Override
    void forEach(Mask<Float, Shapes.S256Bit> o, FUnCon f) {
        boolean[] mbits = ((Float256Mask)o).getBits();
        forEach((i, a) -> {
            if (mbits[i]) { f.apply(i, a); }
        });
    }

    Int256Vector toBits() {
        float[] vec = getElements();
        int[] res = new int[this.species().length()];
        for(int i = 0; i < this.species().length(); i++){
            res[i] = Float.floatToIntBits(vec[i]);
        }
        return new Int256Vector(res);
    }


    @Override
    public Float256Vector rotateEL(int j) {
        float[] vec = getElements();
        float[] res = new float[length()];
        for (int i = 0; i < length(); i++){
            res[(j + i) % length()] = vec[i];
        }
        return new Float256Vector(res);
    }

    @Override
    public Float256Vector rotateER(int j) {
        float[] vec = getElements();
        float[] res = new float[length()];
        for (int i = 0; i < length(); i++){
            int z = i - j;
            if(j < 0) {
                res[length() + z] = vec[i];
            } else {
                res[z] = vec[i];
            }
        }
        return new Float256Vector(res);
    }

    @Override
    public Float256Vector shiftEL(int j) {
        float[] vec = getElements();
        float[] res = new float[length()];
        for (int i = 0; i < length() - j; i++) {
            res[i] = vec[i + j];
        }
        return new Float256Vector(res);
    }

    @Override
    public Float256Vector shiftER(int j) {
        float[] vec = getElements();
        float[] res = new float[length()];
        for (int i = 0; i < length() - j; i++){
            res[i + j] = vec[i];
        }
        return new Float256Vector(res);
    }

    @Override
    public Float256Vector shuffle(Vector<Float, Shapes.S256Bit> o, Shuffle<Float, Shapes.S256Bit> s) {
        Float256Vector v = (Float256Vector) o;
        return uOp((i, a) -> {
            float[] vec = this.getElements();
            int e = s.getElement(i);
            if(e >= 0 && e < length()) {
                //from this
                return vec[e];
            } else if(e < length() * 2) {
                //from o
                return v.getElements()[e - length()];
            } else {
                throw new ArrayIndexOutOfBoundsException("Bad reordering for shuffle");
            }
        });
    }

    @Override
    public Float256Vector swizzle(Shuffle<Float, Shapes.S256Bit> s) {
        return uOp((i, a) -> {
            float[] vec = this.getElements();
            int e = s.getElement(i);
            if(e >= 0 && e < length()) {
                return vec[e];
            } else {
                throw new ArrayIndexOutOfBoundsException("Bad reordering for shuffle");
            }
        });
    }

    @Override
    @ForceInline
    public Float256Vector blend(Vector<Float, Shapes.S256Bit> o1, Mask<Float, Shapes.S256Bit> o2) {
        Objects.requireNonNull(o1);
        Objects.requireNonNull(o2);
        Float256Vector v = (Float256Vector)o1;
        Float256Mask   m = (Float256Mask)o2;

        return (Float256Vector) VectorIntrinsics.blend(
            Float256Vector.class, Float256Mask.class, float.class, LENGTH,
            this, v, m,
            (v1, v2, m_) -> v1.bOp(v2, (i, a, b) -> m_.getElement(i) ? b : a));
    }

    // Accessors

    @Override
    public float get(int i) {
        float[] vec = getElements();
        return vec[i];
    }

    @Override
    public Float256Vector with(int i, float e) {
        float[] res = vec.clone();
        res[i] = e;
        return new Float256Vector(res);
    }

    // Mask

    static final class Float256Mask extends AbstractMask<Float, Shapes.S256Bit> {
        static final Float256Mask TRUE_MASK = new Float256Mask(true);
        static final Float256Mask FALSE_MASK = new Float256Mask(false);

        // FIXME: was temporarily put here to simplify rematerialization support in the JVM
        private final boolean[] bits; // Don't access directly, use getBits() instead.

        public Float256Mask(boolean[] bits) {
            this(bits, 0);
        }

        public Float256Mask(boolean[] bits, int i) {
            this.bits = Arrays.copyOfRange(bits, i, i + species().length());
        }

        public Float256Mask(boolean val) {
            boolean[] bits = new boolean[species().length()];
            Arrays.fill(bits, val);
            this.bits = bits;
        }

        boolean[] getBits() {
            return VectorIntrinsics.maybeRebox(this).bits;
        }

        @Override
        Float256Mask uOp(MUnOp f) {
            boolean[] res = new boolean[species().length()];
            boolean[] bits = getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = f.apply(i, bits[i]);
            }
            return new Float256Mask(res);
        }

        @Override
        Float256Mask bOp(Mask<Float, Shapes.S256Bit> o, MBinOp f) {
            boolean[] res = new boolean[species().length()];
            boolean[] bits = getBits();
            boolean[] mbits = ((Float256Mask)o).getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = f.apply(i, bits[i], mbits[i]);
            }
            return new Float256Mask(res);
        }

        @Override
        public Float256Species species() {
            return SPECIES;
        }

        @Override
        public Float256Vector toVector() {
            float[] res = new float[species().length()];
            boolean[] bits = getBits();
            for (int i = 0; i < species().length(); i++) {
                res[i] = (float) (bits[i] ? -1 : 0);
            }
            return new Float256Vector(res);
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <Z> Mask<Z, Shapes.S256Bit> rebracket(Species<Z, Shapes.S256Bit> species) {
            Objects.requireNonNull(species);
            // TODO: check proper element type
            return VectorIntrinsics.reinterpret(
                Float256Mask.class, float.class, LENGTH,
                species.elementType(), species.length(), this,
                (m, t) -> m.reshape(species)
            );
        }

        // Unary operations

        @Override
        @ForceInline
        public Float256Mask not() {
            return (Float256Mask) VectorIntrinsics.unaryOp(
                                             VECTOR_OP_NOT, Float256Mask.class, int.class, LENGTH,
                                             this,
                                             (m1) -> m1.uOp((i, a) -> !a));
        }

        // Binary operations

        @Override
        @ForceInline
        public Float256Mask and(Mask<Float,Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            Float256Mask m = (Float256Mask)o;
            return VectorIntrinsics.binaryOp(VECTOR_OP_AND, Float256Mask.class, int.class, LENGTH,
                                             this, m,
                                             (m1, m2) -> m1.bOp(m2, (i, a, b) -> a & b));
        }

        @Override
        @ForceInline
        public Float256Mask or(Mask<Float,Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            Float256Mask m = (Float256Mask)o;
            return VectorIntrinsics.binaryOp(VECTOR_OP_OR, Float256Mask.class, int.class, LENGTH,
                                             this, m,
                                             (m1, m2) -> m1.bOp(m2, (i, a, b) -> a | b));
        }

        // Reductions

        @Override
        @ForceInline
        public boolean anyTrue() {
            return VectorIntrinsics.test(COND_notZero, Float256Mask.class, int.class, LENGTH,
                                         this, this,
                                         (m1, m2) -> super.anyTrue());
        }

        @Override
        @ForceInline
        public boolean allTrue() {
            return VectorIntrinsics.test(COND_carrySet, Float256Mask.class, int.class, LENGTH,
                                         this, species().maskAllTrue(),
                                         (m1, m2) -> super.allTrue());
        }
    }

    // Shuffle

    static final class Float256Shuffle extends AbstractShuffle<Float, Shapes.S256Bit> {
        static final IntVector.IntSpecies<Shapes.S256Bit> INT_SPECIES = IntVector.species(Shapes.S_256_BIT);

        public Float256Shuffle(int[] reorder) {
            super(reorder);
        }

        public Float256Shuffle(int[] reorder, int i) {
            super(reorder, i);
        }

        @Override
        public Float256Species species() {
            return SPECIES;
        }

        @Override
        public IntVector.IntSpecies<Shapes.S256Bit> intSpecies() {
            return INT_SPECIES;
        }
    }

    // Species

    @Override
    public Float256Species species() {
        return SPECIES;
    }

    static final class Float256Species extends FloatSpecies<Shapes.S256Bit> {
        static final int BIT_SIZE = Shapes.S_256_BIT.bitSize();

        static final int LENGTH = BIT_SIZE / Float.SIZE;

        @Override
        public String toString() {
           StringBuilder sb = new StringBuilder("Shape[");
           sb.append(bitSize()).append(" bits, ");
           sb.append(length()).append(" ").append(float.class.getSimpleName()).append("s x ");
           sb.append(elementSize()).append(" bits");
           sb.append("]");
           return sb.toString();
        }

        @Override
        @ForceInline
        public int bitSize() {
            return BIT_SIZE;
        }

        @Override
        @ForceInline
        public int length() {
            return LENGTH;
        }

        @Override
        @ForceInline
        public Class<Float> elementType() {
            return float.class;
        }

        @Override
        @ForceInline
        public int elementSize() {
            return Float.SIZE;
        }

        @Override
        @ForceInline
        public Shapes.S256Bit shape() {
            return Shapes.S_256_BIT;
        }

        @Override
        Float256Vector op(FOp f) {
            float[] res = new float[length()];
            for (int i = 0; i < length(); i++) {
                res[i] = f.apply(i);
            }
            return new Float256Vector(res);
        }

        @Override
        Float256Vector op(Mask<Float, Shapes.S256Bit> o, FOp f) {
            float[] res = new float[length()];
            boolean[] mbits = ((Float256Mask)o).getBits();
            for (int i = 0; i < length(); i++) {
                if (mbits[i]) {
                    res[i] = f.apply(i);
                }
            }
            return new Float256Vector(res);
        }

        // Factories

        @Override
        public Float256Mask maskFromValues(boolean... bits) {
            return new Float256Mask(bits);
        }

        @Override
        public Float256Mask maskFromArray(boolean[] bits, int i) {
            return new Float256Mask(bits, i);
        }

        @Override
        public Float256Shuffle shuffleFromValues(int... ixs) {
            return new Float256Shuffle(ixs);
        }

        @Override
        public Float256Shuffle shuffleFromArray(int[] ixs, int i) {
            return new Float256Shuffle(ixs, i);
        }

        @Override
        public Float256Shuffle shuffleFromVector(Vector<Integer, Shapes.S256Bit> v) {
            int[] a = ((IntVector<Shapes.S256Bit>) v).toArray();
            return new Float256Shuffle(a, 0);
        }

        @Override
        @ForceInline
        public Float256Vector zero() {
            return VectorIntrinsics.broadcastCoerced(Float256Vector.class, float.class, LENGTH,
                                                     Float.floatToIntBits(0.0f),
                                                     (z -> ZERO));
        }

        @Override
        @ForceInline
        public Float256Vector broadcast(float e) {
            return VectorIntrinsics.broadcastCoerced(
                Float256Vector.class, float.class, LENGTH,
                Float.floatToIntBits(e),
                ((long bits) -> SPECIES.op(i -> Float.intBitsToFloat((int)bits))));
        }

        @Override
        @ForceInline
        public Float256Mask maskAllTrue() {
            return VectorIntrinsics.broadcastCoerced(Float256Mask.class, int.class, LENGTH,
                                                     (int)-1,
                                                     (z -> Float256Mask.TRUE_MASK));
        }

        @Override
        @ForceInline
        public Float256Mask maskAllFalse() {
            return VectorIntrinsics.broadcastCoerced(Float256Mask.class, int.class, LENGTH,
                                                     0,
                                                     (z -> Float256Mask.FALSE_MASK));
        }

        @Override
        @ForceInline
        public Float256Vector scalars(float... es) {
            Objects.requireNonNull(es);
            int ix = VectorIntrinsics.checkIndex(0, es.length, LENGTH);
            return (Float256Vector) VectorIntrinsics.load(Float256Vector.class, float.class, LENGTH,
                                                        es, ix,
                                                        (arr, idx) -> super.fromArray((float[]) arr, idx));
        }

        @Override
        @ForceInline
        public Float256Vector fromArray(float[] a, int ix) {
            Objects.requireNonNull(a);
            ix = VectorIntrinsics.checkIndex(ix, a.length, LENGTH);
            return (Float256Vector) VectorIntrinsics.load(Float256Vector.class, float.class, LENGTH,
                                                        a, ix,
                                                        (arr, idx) -> super.fromArray((float[]) arr, idx));
        }

        @Override
        @ForceInline
        public Float256Vector fromArray(float[] a, int ax, Mask<Float, Shapes.S256Bit> m) {
            return zero().blend(fromArray(a, ax), m); // TODO: use better default impl: op(m, i -> a[ax + i]);
        }

        @Override
        @ForceInline
        public Float256Vector fromByteArray(byte[] a, int ix) {
            Objects.requireNonNull(a);
            ix = VectorIntrinsics.checkIndex(ix, a.length, bitSize() / Byte.SIZE);
            return (Float256Vector) VectorIntrinsics.load(Float256Vector.class, float.class, LENGTH,
                                                        a, ix,
                                                        (arr, idx) -> super.fromByteArray((byte[]) arr, idx));
        }

        @Override
        @ForceInline
        public Float256Vector fromByteArray(byte[] a, int ix, Mask<Float, Shapes.S256Bit> m) {
            return zero().blend(fromByteArray(a, ix), m);
        }

        @Override
        @ForceInline
        public Float256Vector fromByteBuffer(ByteBuffer bb) {
            if (bb.hasArray() && !bb.isReadOnly() && bb.order() == ByteOrder.nativeOrder()) {
                int num_bytes = bitSize() / Byte.SIZE;
                int ix = VectorIntrinsics.checkIndex(bb.position(), bb.limit(), num_bytes);
                return (Float256Vector) VectorIntrinsics.load(Float256Vector.class, float.class, LENGTH,
                                                            bb.array(), ix,
                                                            (arr, idx) -> super.fromByteArray((byte[]) arr, idx));
            } else {
                return (Float256Vector)super.fromByteBuffer(bb);
            }
        }

        @Override
        @ForceInline
        public Float256Vector fromByteBuffer(ByteBuffer bb, Mask<Float, Shapes.S256Bit> m) {
            return zero().blend(fromByteBuffer(bb), m);
        }

        @Override
        @ForceInline
        public Float256Vector fromByteBuffer(ByteBuffer bb, int ix) {
            if (bb.hasArray() && !bb.isReadOnly() && bb.order() == ByteOrder.nativeOrder()) {
                int num_bytes = bitSize() / Byte.SIZE;
                int ax = VectorIntrinsics.checkIndex(ix, bb.limit(), num_bytes);
                return (Float256Vector) VectorIntrinsics.load(Float256Vector.class, float.class, LENGTH,
                                                            bb.array(), ax,
                                                            (arr, idx) -> super.fromByteArray((byte[]) arr, idx));
            } else {
                return (Float256Vector)super.fromByteBuffer(bb, ix);
            }
        }

        @Override
        @ForceInline
        public Float256Vector fromByteBuffer(ByteBuffer bb, int ix, Mask<Float, Shapes.S256Bit> m) {
            return zero().blend(fromByteBuffer(bb, ix), m);
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Float256Vector castFromByte(ByteVector<S> o) {
            if (o.bitSize() == 64) {
                Byte64Vector so = (Byte64Vector)o;
                return VectorIntrinsics.cast(
                    Byte64Vector.class, byte.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Byte128Vector so = (Byte128Vector)o;
                return VectorIntrinsics.cast(
                    Byte128Vector.class, byte.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Byte256Vector so = (Byte256Vector)o;
                return VectorIntrinsics.cast(
                    Byte256Vector.class, byte.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Byte512Vector so = (Byte512Vector)o;
                return VectorIntrinsics.cast(
                    Byte512Vector.class, byte.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Float256Vector castFromShort(ShortVector<S> o) {
            if (o.bitSize() == 64) {
                Short64Vector so = (Short64Vector)o;
                return VectorIntrinsics.cast(
                    Short64Vector.class, short.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Short128Vector so = (Short128Vector)o;
                return VectorIntrinsics.cast(
                    Short128Vector.class, short.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Short256Vector so = (Short256Vector)o;
                return VectorIntrinsics.cast(
                    Short256Vector.class, short.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Short512Vector so = (Short512Vector)o;
                return VectorIntrinsics.cast(
                    Short512Vector.class, short.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Float256Vector castFromInt(IntVector<S> o) {
            if (o.bitSize() == 64) {
                Int64Vector so = (Int64Vector)o;
                return VectorIntrinsics.cast(
                    Int64Vector.class, int.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Int128Vector so = (Int128Vector)o;
                return VectorIntrinsics.cast(
                    Int128Vector.class, int.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Int256Vector so = (Int256Vector)o;
                return VectorIntrinsics.cast(
                    Int256Vector.class, int.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Int512Vector so = (Int512Vector)o;
                return VectorIntrinsics.cast(
                    Int512Vector.class, int.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Float256Vector castFromLong(LongVector<S> o) {
            if (o.bitSize() == 64) {
                Long64Vector so = (Long64Vector)o;
                return VectorIntrinsics.cast(
                    Long64Vector.class, long.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Long128Vector so = (Long128Vector)o;
                return VectorIntrinsics.cast(
                    Long128Vector.class, long.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Long256Vector so = (Long256Vector)o;
                return VectorIntrinsics.cast(
                    Long256Vector.class, long.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Long512Vector so = (Long512Vector)o;
                return VectorIntrinsics.cast(
                    Long512Vector.class, long.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Float256Vector castFromFloat(FloatVector<S> o) {
            if (o.bitSize() == 64) {
                Float64Vector so = (Float64Vector)o;
                return VectorIntrinsics.cast(
                    Float64Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Float128Vector so = (Float128Vector)o;
                return VectorIntrinsics.cast(
                    Float128Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Float256Vector so = (Float256Vector)o;
                return VectorIntrinsics.cast(
                    Float256Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Float512Vector so = (Float512Vector)o;
                return VectorIntrinsics.cast(
                    Float512Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @ForceInline
        @SuppressWarnings("unchecked")
        private <S extends Shape> Float256Vector castFromDouble(DoubleVector<S> o) {
            if (o.bitSize() == 64) {
                Double64Vector so = (Double64Vector)o;
                return VectorIntrinsics.cast(
                    Double64Vector.class, double.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 128) {
                Double128Vector so = (Double128Vector)o;
                return VectorIntrinsics.cast(
                    Double128Vector.class, double.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 256) {
                Double256Vector so = (Double256Vector)o;
                return VectorIntrinsics.cast(
                    Double256Vector.class, double.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else if (o.bitSize() == 512) {
                Double512Vector so = (Double512Vector)o;
                return VectorIntrinsics.cast(
                    Double512Vector.class, double.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)super.cast(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <E, S extends Shape> Float256Vector cast(Vector<E, S> o) {
            Objects.requireNonNull(o);
            if (o.elementType() == byte.class) {
                ByteVector<S> so = (ByteVector<S>)o;
                return castFromByte(so);
            } else if (o.elementType() == short.class) {
                ShortVector<S> so = (ShortVector<S>)o;
                return castFromShort(so);
            } else if (o.elementType() == int.class) {
                IntVector<S> so = (IntVector<S>)o;
                return castFromInt(so);
            } else if (o.elementType() == long.class) {
                LongVector<S> so = (LongVector<S>)o;
                return castFromLong(so);
            } else if (o.elementType() == float.class) {
                FloatVector<S> so = (FloatVector<S>)o;
                return castFromFloat(so);
            } else if (o.elementType() == double.class) {
                DoubleVector<S> so = (DoubleVector<S>)o;
                return castFromDouble(so);
            } else {
                throw new InternalError("Unimplemented type");
            }
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <F> Float256Vector rebracket(Vector<F, Shapes.S256Bit> o) {
            Objects.requireNonNull(o);
            if (o.elementType() == byte.class) {
                Byte256Vector so = (Byte256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Byte256Vector.class, byte.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else if (o.elementType() == short.class) {
                Short256Vector so = (Short256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Short256Vector.class, short.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else if (o.elementType() == int.class) {
                Int256Vector so = (Int256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Int256Vector.class, int.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else if (o.elementType() == long.class) {
                Long256Vector so = (Long256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Long256Vector.class, long.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else if (o.elementType() == float.class) {
                Float256Vector so = (Float256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Float256Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else if (o.elementType() == double.class) {
                Double256Vector so = (Double256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Double256Vector.class, double.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else {
                throw new InternalError("Unimplemented type");
            }
        }

        @Override
        @ForceInline
        @SuppressWarnings("unchecked")
        public <T extends Shape> Float256Vector resize(Vector<Float, T> o) {
            Objects.requireNonNull(o);
            if (o.bitSize() == 64) {
                Float64Vector so = (Float64Vector)o;
                return VectorIntrinsics.reinterpret(
                    Float64Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else if (o.bitSize() == 128) {
                Float128Vector so = (Float128Vector)o;
                return VectorIntrinsics.reinterpret(
                    Float128Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else if (o.bitSize() == 256) {
                Float256Vector so = (Float256Vector)o;
                return VectorIntrinsics.reinterpret(
                    Float256Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else if (o.bitSize() == 512) {
                Float512Vector so = (Float512Vector)o;
                return VectorIntrinsics.reinterpret(
                    Float512Vector.class, float.class, so.length(),
                    float.class, LENGTH, so,
                    (v, t) -> (Float256Vector)reshape(v)
                );
            } else {
                throw new InternalError("Unimplemented size");
            }
        }
    }
}

import org.checkerframework.checker.nullness.qual.*;

class Pair<S extends @Nullable Object, T extends @Nullable Object> {
    static <U extends @Nullable Object, V extends @Nullable Object>
    Pair<U, V> of(U p1, V p2) { return new Pair<U, V>(); }
}

class PairSub<SS extends @Nullable Object, TS extends @Nullable Object>
    extends Pair<SS, TS> {
    static <US extends @Nullable Object, VS extends @Nullable Object>
    PairSub<US, VS> of(US p1, VS p2) { return new PairSub<US, VS>(); }
}

class PairSubSwitching<SS extends @Nullable Object, TS extends @Nullable Object>
    extends Pair<TS, SS> {
    static <US extends @Nullable Object, VS extends @Nullable Object>
    PairSubSwitching<US, VS> of2(US p1, VS p2) { return new PairSubSwitching<US, VS>(); }
}

class Test1<X extends @Nullable Object> {
    Pair<@Nullable X, @Nullable X> test1(@Nullable X p) {
        return Pair.<@Nullable X, @Nullable X>of(p, (X) null);
    }
}

class Test2<X extends @Nullable Object> {
    Pair<@Nullable X, @Nullable X> test1(@Nullable X p) {
        return Pair.of(p, (@Nullable X) null);
    }
    /*
    Pair<@Nullable X, @Nullable X> test2(@Nullable X p) {
        // TODO cast: should this X mean the same as above??
        return Pair.of(p, (X) null);
    }
    */
}

class Test3<X extends @Nullable Object> {
    Pair<@NonNull X, @NonNull X> test1(@Nullable X p) {
        //:: error: (return.type.incompatible)
        return Pair.of(p, (X) null);
    }
}

class Test4 {
    Pair<@Nullable String, Integer> psi = PairSub.of("Hi", 42);
    Pair<@Nullable String, Integer> psi2 = PairSub.of(null, 42);
    //:: error: (assignment.type.incompatible)
    Pair<String, Integer> psi3 = PairSub.of(null, 42);

    Pair<@Nullable String, Integer> psisw = PairSubSwitching.of2(42, null);
    //:: error: (assignment.type.incompatible)
    Pair<String, Integer> psisw2 = PairSubSwitching.of2(42, null);
}

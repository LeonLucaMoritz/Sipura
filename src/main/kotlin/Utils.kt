object Utils {

    /**
     * This method runs faster than creating the set that contains
     * the intersection of [s1] and [s2] and taking the size of it.
     * It works by taking the smaller set of [s1] and [s2],
     * and then counting how many elements are contained in the other set.
     *
     * runtime: O( min( |s1|, |s2| ) )
     *
     * @return This size of the union of [s1] and [s1].
     */
    fun <E> intersectionSize(s1: Set<E>, s2: Set<E>): Int =
        if (s1.size < s2.size)
            s1.count { it in s2 }
        else
            s2.count { it in s1 }

    /**
     * This method runs faster than creating the set that contains
     * the union of [s1] and [s2] and taking the size of it.
     * It works by taking the smaller set of [s1] and [s2],
     * and then counting how many elements are not contained in the other set.
     * Then the result plus the size of the other set is returned.
     *
     * runtime: O( min( |s1|, |s2| ) )
     *
     * @return This size of the union of [s1] and [s1].
     */
    fun <E> unionSize(s1: Set<E>, s2: Set<E>): Int =
        if (s1.size < s2.size)
            s2.size + s1.count { it !in s2 }
        else
            s1.size + s2.count { it !in s1 }

    fun <E> isDisjoint(s1: Set<E>, s2: Set<E>): Boolean =
        if (s1.size < s2.size)
            s1.none { it in s2 }
        else
            s2.none { it in s1 }

}
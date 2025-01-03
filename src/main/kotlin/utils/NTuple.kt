package de.sschellhoff.utils


data class NTuple2<T1, T2>(val t1: T1, val t2: T2)

data class NTuple3<T1, T2, T3>(val t1: T1, val t2: T2, val t3: T3)

data class NTuple4<T1, T2, T3, T4>(val t1: T1, val t2: T2, val t3: T3, val t4: T4)

infix fun <T1, T2> T1.then(t2: T2): NTuple2<T1, T2>
{
    return NTuple2(this, t2)
}

infix fun <T1, T2, T3> NTuple2<T1, T2>.then(t3: T3): NTuple3<T1, T2, T3>
{
    return NTuple3(this.t1, this.t2, t3)
}

infix fun <T1, T2, T3, T4> NTuple3<T1, T2, T3>.then(t4: T4): NTuple4<T1, T2, T3, T4>
{
    return NTuple4(this.t1, this.t2, this.t3, t4)
}

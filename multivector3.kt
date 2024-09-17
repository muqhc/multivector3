import kotlin.math.abs

data class Multivector3(
    var c: Double = 0.0, var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0,
    var xy: Double = 0.0, var yz: Double = 0.0, var zx: Double = 0.0, var xyz: Double = 0.0
) {
    companion object {
        val Zero get() = Multivector3()

        val I1 get() = Multivector3(c=1.0)

        val Ix get() = Multivector3(x=1.0)
        val Iy get() = Multivector3(y=1.0)
        val Iz get() = Multivector3(z=1.0)

        val Ixy get() = Multivector3(xy=1.0)
        val Iyz get() = Multivector3(yz=1.0)
        val Izx get() = Multivector3(zx=1.0)

        val Iyx get() = Multivector3(xy=-1.0)
        val Izy get() = Multivector3(yz=-1.0)
        val Ixz get() = Multivector3(zx=-1.0)

        val Ixyz get() = Multivector3(xyz=1.0)
    }

    var yx: Double 
        get() = -xy
        set(i) { xy = - i }
    var zy: Double 
        get() = -yz
        set(i) { yz = - i }
    var xz: Double 
        get() = -zx
        set(i) { zx = - i }
    
    fun graded(grade: Int) = when (grade) {
        0 -> Multivector3(c=c)
        1 -> Multivector3(x=x, y=y, z=z)
        2 -> Multivector3(xy=xy, yz=yz, zx=zx)
        3 -> Multivector3(xyz=xyz)
        else -> Zero
    }

    operator fun plus(v: Multivector3) = Multivector3(
    	c+v.c, x+v.x, y+v.y, z+v.z,
    	xy+v.xy, yz+v.yz, zx+v.zx, xyz+v.xyz
    )

    operator fun unaryMinus() = times(-1.0)

    operator fun minus(v: Multivector3) = plus(-v)
    
    operator fun times(a: Double) = Multivector3(
    	c*a, x*a, y*a, z*a,
    	xy*a, yz*a, zx*a, xyz*a
    )
    
    operator fun times(v: Multivector3) = listOf(
        this*v.c,
        
        Multivector3(c=v.x*x, x=v.x*c, y=v.x*-xy, z=v.x*zx, xy=v.x*-y, yz=v.x*xyz, zx=v.x*z, xyz=v.x*yz),
        Multivector3(c=v.y*y, y=v.y*c, z=v.y*-yz, x=v.y*xy, yz=v.y*-z, zx=v.y*xyz, xy=v.y*x, xyz=v.y*zx),
        Multivector3(c=v.z*z, z=v.z*c, x=v.z*-zx, y=v.z*yz, zx=v.z*-x, xy=v.z*xyz, yz=v.z*y, xyz=v.z*xy),
        
        Multivector3(c=v.xy*-xy, xy=v.xy*c, yz=v.xy*-zx, zx=v.xy*yz, x=v.xy*-y, y=v.xy*x, z=v.xy*-xyz, xyz=v.xy*z),
        Multivector3(c=v.yz*-yz, yz=v.yz*c, zx=v.yz*-xy, xy=v.yz*zx, y=v.yz*-z, z=v.yz*y, x=v.yz*-xyz, xyz=v.yz*x),
        Multivector3(c=v.zx*-zx, zx=v.zx*c, xy=v.zx*-yz, yz=v.zx*xy, z=v.zx*-x, x=v.zx*z, y=v.zx*-xyz, xyz=v.zx*y),
        
        Multivector3(v.xyz*-xyz, v.xyz*-yz, v.xyz*-zx, v.xyz*-yz, v.xyz*z, v.xyz*x, v.xyz*y, v.xyz*c)
    ).reduce(Multivector3::plus)

    fun copy(init: (Multivector3) -> Unit) = copy().apply(init)

    fun exteriorProduct(v: Multivector3): Multivector3 = (0..3).map { r -> (0..3).map { s ->
        (graded(r)*v.graded(s)).graded(r+s)
    }}.flatten().reduce(Multivector3::plus)

    fun leftContraction(v: Multivector3): Multivector3 = (0..3).map { r -> (0..3).map { s ->
        (graded(r)*v.graded(s)).graded(s-r)
    }}.flatten().reduce(Multivector3::plus)

    fun rightContraction(v: Multivector3): Multivector3 = (0..3).map { r -> (0..3).map { s ->
        (graded(r)*v.graded(s)).graded(r-s)
    }}.flatten().reduce(Multivector3::plus)

    fun scalarProduct(v: Multivector3): Multivector3 = (0..3).map { r -> (0..3).map { s ->
        (graded(r)*v.graded(s)).graded(0)
    }}.flatten().reduce(Multivector3::plus)

    fun dotProduct(v: Multivector3): Multivector3 = (0..3).map { r -> (0..3).map { s ->
        (graded(r)*v.graded(s)).graded(abs(s-r))
    }}.flatten().reduce(Multivector3::plus)

    fun commutatorProduct(v: Multivector3): Multivector3 = 0.5*((this*v) - (v*this))

    fun innerProductG1(v: Multivector3): Multivector3 {
        val graded1 = graded(1)
        val graded2 = v.graded(1)
        return 0.5*((graded1*graded2) + (graded2*graded1))
    }

    fun exteriorProductG1(v: Multivector3): Multivector3 {
        val graded1 = graded(1)
        val graded2 = v.graded(1)
        return 0.5*((graded1*graded2) - (graded2*graded1))
    }
}

operator fun Double.times(v: Multivector3) = v*this


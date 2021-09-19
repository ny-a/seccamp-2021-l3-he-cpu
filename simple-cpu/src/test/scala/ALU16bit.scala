import chisel3.iotesters.{Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.util.control.Breaks


class ALU16bitTester(c: ALU16bit) extends PeekPokeTester(c) {
  val b = new Breaks
  b.breakable {
    for {
      i <- Seq(-32768, -257, 0, 257, 32767)
      j <- Seq(-32768, -257, 0, 257, 32767)
    } {
      poke(c.io.phase, 2)
      poke(c.io.in0, i)
      poke(c.io.in1, j)

      // Opcode ADD
      poke(c.io.opcode, ALUOpcode.ADD.value)
      step(1)
      if (!expect(c.io.dr, (i+j).toShort)) {
        println(s"$i + $j failed")
        b.break
      }

      // Opcode SUB
      poke(c.io.opcode, ALUOpcode.SUB.value)
      step(1)
      if (!expect(c.io.dr, (i-j).toShort)) {
        println(s"$i - $j failed")
        b.break
      }

      poke(c.io.opcode, ALUOpcode.AND.value)
      step(1)
      if (!expect(c.io.dr, (i&j).toShort)) {
        println(s"$i & $j failed")
        b.break
      }

      poke(c.io.opcode, ALUOpcode.OR.value)
      step(1)
      if (!expect(c.io.dr, (i|j).toShort)) {
        println(s"$i | $j failed")
        b.break
      }

      poke(c.io.opcode, ALUOpcode.XOR.value)
      step(1)
      if (!expect(c.io.dr, (i^j).toShort)) {
        println(s"$i ^ $j failed")
        b.break
      }
    }

    for {
      i <- Seq(-32768, -257, 0, 257, 32767)
      j <- 0 until 16
    } {
      poke(c.io.phase, 2)
      poke(c.io.in0, i)
      poke(c.io.in1, j)

      poke(c.io.opcode, ALUOpcode.SLL.value)
      step(1)
      if (!expect(c.io.dr, (i<<j).toShort)) {
        println(s"$i << $j failed")
        b.break
      }

      poke(c.io.opcode, ALUOpcode.SLR.value)
      step(1)

      val rotated = ((i << j) & 0xFFFF) | ((i & 0xFFFF) >> (16 - j))

      if (!expect(c.io.dr, rotated.toShort)) {
        println(s"$i rotate $j failed")
        b.break
      }

      poke(c.io.opcode, ALUOpcode.SRL.value)
      step(1)
      if (!expect(c.io.dr, (i.toShort >>> j).toShort)) {
        println(s"$i >>> $j failed")
        b.break
      }

      poke(c.io.opcode, ALUOpcode.SRA.value)
      step(1)
      if (!expect(c.io.dr, (i>>j).toShort)) {
        println(s"$i >> $j failed")
        b.break
      }
    }
  }
}

class ALU16bitSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    Driver.execute(Array("--backend-name", "firrtl"), () => new ALU16bit()) { c =>
      new ALU16bitTester(c)
    } should be (true)
  }
}

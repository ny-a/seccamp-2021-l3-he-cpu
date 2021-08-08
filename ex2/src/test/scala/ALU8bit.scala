import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.util.control.Breaks


class ALU8bitTester(c: ALU8bit) extends PeekPokeTester(c) {
  val b = new Breaks
  b.breakable {
    for {
      i <- 0 until 255
      j <- 0 until 255
    } {
      poke(c.io.inA, i)
      poke(c.io.inB, j)

      // Opcode ADD
      poke(c.io.opcode, 0)
      if (!expect(c.io.out, (i+j)&0xFF)) {
        b.break
      }

      // Opcode SUB
      poke(c.io.opcode, 1)
      if (!expect(c.io.out, (i-j)&0xFF)) {
        b.break
      }

      // Opcode AND
      poke(c.io.opcode, 2)
      if (!expect(c.io.out, (i&j)&0xFF)) {
        b.break
      }

      // Opcode XOR
      poke(c.io.opcode, 3)
      if (!expect(c.io.out, (i^j)&0xFF)) {
        b.break
      }

      // Opcode OR
      poke(c.io.opcode, 4)
      if (!expect(c.io.out, (i|j)&0xFF)) {
        b.break
      }
    }
  }
}

class ALU8bitSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    Driver.execute(Array("--backend-name", "firrtl"), () => new ALU8bit()) { c =>
      new ALU8bitTester(c)
    } should be (true)
  }
}
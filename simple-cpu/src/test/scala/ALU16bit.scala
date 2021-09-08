import chisel3.iotesters.{Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.util.control.Breaks


class ALU16bitTester(c: ALU16bit) extends PeekPokeTester(c) {
  val b = new Breaks
  b.breakable {
    for {
      i <- 0 until 257
      j <- 0 until 257
    } {
      poke(c.io.phase, 2)
      poke(c.io.in0, i)
      poke(c.io.in1, j)

      // Opcode ADD
      poke(c.io.opcode, ALUOpcode.ADD.value)
      step(1)
      if (!expect(c.io.dr, (i+j)&0xFFFF)) {
        b.break
      }

      // Opcode SUB
      poke(c.io.opcode, ALUOpcode.SUB.value)
      step(1)
      if (!expect(c.io.dr, (i-j)&0xFFFF)) {
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

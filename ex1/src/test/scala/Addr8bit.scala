import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class Addr8bitTester(c: Addr8bit) extends PeekPokeTester(c) {
  for {
    i <- 0 until 255
    j <- 0 until 255
  } {
    poke(c.io.inA, i)
    poke(c.io.inB, j)
    expect(c.io.out, (i+j)&0xFF)
  }
}

class Addr8bitSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    Driver.execute(Array("--backend-name", "firrtl"), () => new Addr8bit()) { c =>
      new Addr8bitTester(c)
    } should be (true)
  }
}
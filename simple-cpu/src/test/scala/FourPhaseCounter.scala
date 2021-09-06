import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.math.log
import scala.math.ceil

class FourPhaseConuterTest(c: FourPhaseCounter) extends PeekPokeTester(c) {
  expect(c.io.out, 1<<0)
  step(1)
  expect(c.io.out, 1<<1)
  step(1)
  expect(c.io.out, 1<<2)
  step(1)
  expect(c.io.out, 1<<3)
  step(5)
  expect(c.io.out, 1<<0)
}

class FourPhaseCounterSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    Driver.execute(Array("--backend-name", "firrtl"), () => new FourPhaseCounter()) { c =>
      new FourPhaseConuterTest(c)
    } should be (true)
  }
}

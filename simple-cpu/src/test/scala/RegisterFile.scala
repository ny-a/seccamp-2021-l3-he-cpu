import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.math.log
import scala.math.ceil

class RegisterFileTest(c: RegisterFile) extends PeekPokeTester(c) {
  poke(c.io.phase, 2)
  poke(c.io.writeEnabled, 1)
  poke(c.io.writeSelect, 1)
  poke(c.io.readSelect0, 0)
  poke(c.io.readSelect1, 1)
  poke(c.io.drValue, 255)
  expect(c.io.out0, 0)
  expect(c.io.out1, 0)
  step(1)
  poke(c.io.phase, 3)
  expect(c.io.out0, 0)
  expect(c.io.out1, 0)
  step(1)
  poke(c.io.phase, 0)
  expect(c.io.out0, 0)
  expect(c.io.out1, 255)
  step(1)
  poke(c.io.phase, 1)
  expect(c.io.out0, 0)
  expect(c.io.out1, 255)
}

class RegisterFileSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    Driver.execute(Array("--backend-name", "firrtl"), () => new RegisterFile()) { c =>
      new RegisterFileTest(c)
    } should be (true)
  }
}

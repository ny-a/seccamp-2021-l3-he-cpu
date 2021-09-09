import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class TopCore(val n: Int) extends Module {
    val io = IO(new Bundle{
        val out = Output(SInt(16.W))
    })

    val data = Seq(
      0x8101, 0x8201, 0x8300 + n, 0xcb10, 0xb943, 0xd000, 0xcb10, 0xb93f, 0xc200,
      0xcb10, 0xb93d, 0xd000, 0xcb10, 0xb939, 0xc200, 0xcb10, 0xb937, 0xd000,
      0xcb10, 0xb933, 0xc200, 0xcb10, 0xb931, 0xd000, 0xcb10, 0xb92d, 0xc200,
      0xcb10, 0xb92b, 0xd000, 0xcb10, 0xb927, 0xc200, 0xcb10, 0xb925, 0xd000,
      0xcb10, 0xb921, 0xc200, 0xcb10, 0xb91f, 0xd000, 0xcb10, 0xb91b, 0xc200,
      0xcb10, 0xb919, 0xd000, 0xcb10, 0xb915, 0xc200, 0xcb10, 0xb913, 0xd000,
      0xcb10, 0xb90f, 0xc200, 0xcb10, 0xb90d, 0xd000, 0xcb10, 0xb909, 0xc200,
      0xcb10, 0xb907, 0xd000, 0xcb10, 0xb903, 0xc200, 0xcb10, 0xb901, 0xd060,
      0xa0ff
    )

    val rom = Module(new ExternalRom(data))
    val core = Module(new Core())

    rom.io.addr := core.io.romAddr
    core.io.romData := rom.io.data
    io.out := core.io.out
}

class CoreTester(c: TopCore, val n: Int) extends PeekPokeTester(c) {
  def fib(i: Int): Int = {
    if (i == 0) {
      return 0
    }
    if (i == 1) {
      return 1
    }
    return fib(i - 2) + fib(i - 1)
  }
  val answer = fib(n)
  step(28 + 12 * n)
  println(answer.toString)
  expect(c.io.out, answer)
}

class CoreSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    for {
      n <- 0 until 23
    } {
      println(n)
      Driver.execute(Array("--backend-name", "firrtl"), () => new TopCore(n)) { c =>
        new CoreTester(c, n)
      } should be (true)
    }
  }
}

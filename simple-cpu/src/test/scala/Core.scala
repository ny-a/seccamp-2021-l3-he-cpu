import chisel3._
import chisel3.iotesters.{Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class TopCore(val n: Int) extends Module {
    val io = IO(new Bundle{
        val out = Output(UInt(16.W))
    })

    val data = Seq(0x8000, 0x8101, 0x8200, 0x8301, 0x8400 + n, 0xc4c0, 0xcc00,
      0xd060, 0xda00, 0xcc10, 0xb805, 0xd860, 0xd300, 0xcc10, 0xb801,
      0xa0f7, 0xc0d0, 0xc0f0, 0xa0ff
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
  step(80 + 20 * n)
  println((answer&0xFFFF).toString)
  expect(c.io.out, answer&0xFFFF)
}

class CoreSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    for {
      n <- 0 until 10
    } {
      println(n)
      Driver.execute(Array("--backend-name", "firrtl"), () => new TopCore(n)) { c =>
        new CoreTester(c, n)
      } should be (true)
    }
  }
}

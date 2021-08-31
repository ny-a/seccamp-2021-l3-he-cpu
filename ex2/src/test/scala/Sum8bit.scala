import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.math.log
import scala.math.ceil

class TopSum16bit(val d:Seq[Int]) extends Module {
    val io = IO(new Bundle{
        val out    = Output(UInt(16.W))
    })

    var log2 = (x: Double) => log(x)/log(2.0)
    val romAddrWidth = ceil(log2(d.size)).asInstanceOf[Int]

    val rom = Module(new ExternalRom(romAddrWidth, d))
    val summer = Module(new Sum16bit(d.size, romAddrWidth))

    rom.io.addr := summer.io.romAddr
    summer.io.romData := rom.io.data
    io.out := summer.io.out
}

class Sum16bitTester(c: TopSum16bit, val d:Seq[Int]) extends PeekPokeTester(c) {
  var sum:Int = 0
  for (i <- 0 until d.size) {
    sum += d(i)
    step(1)
  }
  step(10)
  println((sum&0xFFFF).toString)
  expect(c.io.out, sum&0xFFFF)
}

class Sum8bitSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    val data = Seq(30, 75, 89, 42, 65, 16, 85, 43, 60)
    Driver.execute(Array("--backend-name", "firrtl"), () => new TopSum16bit(data)) { c =>
      new Sum16bitTester(c, data)
    } should be (true)
  }
}

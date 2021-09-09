import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.math.log
import scala.math.ceil

class TopRomRead(val d:Seq[Int]) extends Module {
    val io = IO(new Bundle{
        val out    = Output(Vec(d.size, UInt(8.W)))
    })

    var log2 = (x: Double) => log(x)/log(2.0)
    val romAddrWidth = ceil(log2(d.size)).asInstanceOf[Int]

    val rom = Module(new ExternalRom(d, romAddrWidth))
    val romReader = Module(new RomRead(8, romAddrWidth, d.size))

    rom.io.addr := romReader.io.addr
    romReader.io.data := rom.io.data
    io.out := romReader.io.out
}

class RomReadTester(c: TopRomRead, val d:Seq[Int]) extends PeekPokeTester(c) {
  step(10)
  println((c.io.out).toString)
  for {
    i <- 0 until d.size
  } {
    expect(c.io.out(i), d(i))
  }
}

class RomReadSpec extends AnyFreeSpec with Matchers {
  "tester should returned values with interpreter" in {
    val data = Seq(30, 75, 89, 42, 65, 16, 85, 43, 60)
    Driver.execute(Array("--backend-name", "firrtl"), () => new TopRomRead(data)) { c =>
      new RomReadTester(c, data)
    } should be (true)
  }
}

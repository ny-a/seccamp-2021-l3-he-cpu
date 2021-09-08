import chisel3._

class ExternalRom(val d: Seq[Int], val addrWidth: Int = 16) extends Module {
  val io = IO(new Bundle{
    val addr = Input(UInt(addrWidth.W))
    val data = Output(UInt(16.W))
  })

  val rom = VecInit(d map (x=> x.U(16.W)))
  io.data := rom(io.addr)
}

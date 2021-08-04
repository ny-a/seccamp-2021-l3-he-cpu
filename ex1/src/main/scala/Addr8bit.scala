import chisel3._

class Addr8bit extends Module {
    val io = IO(new Bundle{
        val inA = Input(UInt(8.W))
        val inB = Input(UInt(8.W))
        val out = Output(UInt(8.W))
    })

    io.out := io.inA + io.inB
}
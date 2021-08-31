import chisel3._

class Counter16bit extends Module {
    val io = IO(new Bundle{
        val out    = Output(UInt(16.W))
    })

    val cnt = RegInit(0.U(16.W))

    io.out := cnt

    when(cnt === 7.U){
        cnt := cnt
    }.otherwise{
        cnt := cnt + 1.U
    }
}

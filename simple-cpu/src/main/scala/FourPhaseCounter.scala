import chisel3._

class FourPhaseCounter extends Module {
    val io = IO(new Bundle{
        val out = Output(UInt(4.W))
    })

    val cnt = RegInit(1.U(4.W))

    io.out := cnt

    when(cnt === 1.U){
        cnt := 2.U
    }.elsewhen(cnt === 2.U){
        cnt := 4.U
    }.elsewhen(cnt === 4.U){
        cnt := 8.U
    }.otherwise{
        cnt := 1.U
    }
}

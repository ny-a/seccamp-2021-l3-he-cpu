import chisel3._

class Sum8bit(val num:Int, val romWidth:Int) extends Module {
    val io = IO(new Bundle{
        val romAddr = Output(UInt(romWidth.W))
        val romData = Input(UInt(8.W))

        val out    = Output(UInt(8.W))
    })

    val sum = RegInit(0.U(8.W))
    val cnt = RegInit(0.U(romWidth.W))

    io.romAddr := cnt
    io.out := sum

    when(cnt === num.U){
      cnt := cnt
      sum := sum
    }.otherwise{
      cnt := cnt + 1.U
      sum := sum + io.romData
    }
}

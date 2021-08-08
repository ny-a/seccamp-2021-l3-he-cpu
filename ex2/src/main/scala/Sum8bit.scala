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
      // ここに処理を記述
    }.otherwise{
      // ここに処理を記述
    }
}
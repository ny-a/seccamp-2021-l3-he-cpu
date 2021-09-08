import chisel3._

class RegisterFile(val registerLength: Int = 8) extends Module {
  val io = IO(new Bundle{
    val phase = Input(UInt(2.W))

    val drValue = Input(UInt(16.W))

    val writeEnabled = Input(Bool())
    val writeSelect = Input(UInt(3.W))

    val readSelect0 = Input(UInt(3.W))
    val readSelect1 = Input(UInt(3.W))
    val out0 = Output(UInt(16.W))
    val out1 = Output(UInt(16.W))
  })

  val register = RegInit(VecInit(Seq.fill(registerLength)(0.U(16.W))))

  io.out0 := register(io.readSelect0)
  io.out1 := register(io.readSelect1)

  when(io.phase === FourPhase.WriteBack && io.writeEnabled === 1.U){
    register(io.writeSelect) := io.drValue
  }
}

import chisel3._
import chisel3.util.BitPat

class InstructionFetcher() extends Module {
  val io = IO(new Bundle{
    val phase = Input(UInt(2.W))
    val isBranching = Input(Bool())
    val drValue = Input(SInt(16.W))

    val romAddr = Output(UInt(16.W))
    val romData = Input(UInt(16.W))

    val ir = Output(UInt(16.W))
    val pcPlus1 = Output(UInt(16.W))
  })

  val pc = RegInit(0.U(16.W))
  val pcPlus1 = RegInit(0.U(16.W))
  val ir = RegInit(0.U(16.W))

  io.romAddr := pc
  io.ir := ir
  io.pcPlus1 := pcPlus1

  pc := pc
  ir := ir
  pcPlus1 := pc + 1.U

  when(io.phase === FourPhase.InstructionFetch){
    ir := io.romData
  }

  when(io.phase === FourPhase.WriteBack){
    when(!io.isBranching) {
      pc := pcPlus1
    }
    when(io.isBranching) {
      pc := io.drValue.asUInt
    }
  }
}

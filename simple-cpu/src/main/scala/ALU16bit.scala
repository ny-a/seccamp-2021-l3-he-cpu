import chisel3._
import chisel3.util.BitPat

object ALUOpcode {
  def ADD = BitPat("b0")
  def SUB = BitPat("b1")
}

class ALU16bit extends Module {
  val io = IO(new Bundle{
    val phase  = Input(UInt(2.W))
    val in0    = Input(UInt(16.W))
    val in1    = Input(UInt(16.W))
    val opcode = Input(Bool())
    val dr     = Output(UInt(16.W))
    val flagZ  = Output(Bool())
  })

  val dr = RegInit(0.U(16.W))
  val flagZ = RegInit(0.U(1.W))

  dr := dr
  io.dr := dr
  io.flagZ := flagZ

  when(io.phase === FourPhase.Execution){
    when(io.opcode === ALUOpcode.ADD){
      val result = io.in0 + io.in1
      dr := result
      flagZ := result === 0.U
    }

    when(io.opcode === ALUOpcode.SUB){
      val result = io.in0 - io.in1
      dr := result
      flagZ := result === 0.U
    }
  }
}

import chisel3._
import chisel3.util.BitPat

object FourPhase {
  def InstructionFetch = BitPat("b00")
  def InstructionDecode = BitPat("b01")
  def Execution = BitPat("b10")
  def WriteBack = BitPat("b11")
}

class FourPhaseCounter extends Module {
  val io = IO(new Bundle{
    val phase = Output(UInt(2.W))
  })

  val phase = RegInit(0.U(2.W))

  io.phase := phase

  phase := phase + 1.U
  when(phase === FourPhase.WriteBack){
    phase := 0.U
  }
}

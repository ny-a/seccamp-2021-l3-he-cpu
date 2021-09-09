import chisel3._
import chisel3.util.BitPat

object BranchCondition {
  def Never = BitPat("b00")
  def Always = BitPat("b01")
  def IfZ = BitPat("b10")
  def IfS = BitPat("b11")
}

class BranchController extends Module {
  val io = IO(new Bundle{
    val phase = Input(UInt(2.W))

    val branchCondition = Input(UInt(2.W))

    val flagZ = Input(Bool())
    val flagS = Input(Bool())

    val isBranching = Output(Bool())
  })

  val flagZ = RegInit(0.U(1.W))
  val flagS = RegInit(0.U(1.W))
  val isBranching = RegInit(0.U(1.W))

  io.isBranching := isBranching

  when(io.phase === FourPhase.Execution){
    isBranching := (
      io.branchCondition === BranchCondition.Always ||
      (io.branchCondition === BranchCondition.IfZ && flagZ === 1.U) ||
      (io.branchCondition === BranchCondition.IfS && flagS === 1.U)
    )
  }

  when(io.phase === FourPhase.WriteBack){
    flagZ := io.flagZ
    flagS := io.flagS
  }
}

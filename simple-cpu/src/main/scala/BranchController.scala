import chisel3._
import chisel3.util.BitPat

object BranchCondition {
  def Never = BitPat("b000")
  def Always = BitPat("b001")
  def IfZ = BitPat("b010")
  def IfS = BitPat("b011")
  def IfSorZ = BitPat("b100")
  def IfNotZ = BitPat("b101")
}

class BranchController extends Module {
  val io = IO(new Bundle{
    val phase = Input(UInt(2.W))

    val branchCondition = Input(UInt(3.W))

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
      (io.branchCondition === BranchCondition.IfS && flagS === 1.U) ||
      (io.branchCondition === BranchCondition.IfSorZ && (flagS === 1.U || flagZ === 1.U)) ||
      (io.branchCondition === BranchCondition.IfNotZ && flagZ === 0.U)
    )
  }

  when(io.phase === FourPhase.WriteBack){
    flagZ := io.flagZ
    flagS := io.flagS
  }
}

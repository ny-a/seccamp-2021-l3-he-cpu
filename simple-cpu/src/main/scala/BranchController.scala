import chisel3._
import chisel3.util.BitPat

object BranchCondition {
  def Never = BitPat("b000")
  def Always = BitPat("b001")
  def IfS = BitPat("b010")
  def IfZ = BitPat("b011")
  def IfSorZ = BitPat("b100")
  def IfNotZ = BitPat("b101")
}

class BranchController extends Module {
  val io = IO(new Bundle{
    val phase = Input(UInt(2.W))

    val branchCondition = Input(UInt(3.W))

    val flagS = Input(Bool())
    val flagZ = Input(Bool())
    val flagC = Input(Bool())
    val flagV = Input(Bool())

    val isBranching = Output(Bool())
  })

  val flagS = RegInit(false.B)
  val flagZ = RegInit(false.B)
  val flagC = RegInit(false.B)
  val flagV = RegInit(false.B)
  val isBranching = RegInit(false.B)

  io.isBranching := isBranching

  when(io.phase === FourPhase.Execution){
    isBranching := (
      io.branchCondition === BranchCondition.Always ||
      (io.branchCondition === BranchCondition.IfS && flagS === 1.U) ||
      (io.branchCondition === BranchCondition.IfZ && flagZ === 1.U) ||
      (io.branchCondition === BranchCondition.IfSorZ && (flagS === 1.U || flagZ === 1.U)) ||
      (io.branchCondition === BranchCondition.IfNotZ && flagZ === 0.U)
    )
  }

  when(io.phase === FourPhase.WriteBack){
    flagS := io.flagS
    flagZ := io.flagZ
    flagC := io.flagC
    flagV := io.flagV
  }
}

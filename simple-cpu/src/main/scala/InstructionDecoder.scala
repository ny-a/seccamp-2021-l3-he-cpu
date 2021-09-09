import chisel3._
import chisel3.util.{BitPat, Cat, Fill}

object OpCode1 {
  def LD = BitPat("b00??_????_????_????")
  def ST = BitPat("b01??_????_????_????")
  def Branch = BitPat("b10??_????_????_????")
  def ComputeIO = BitPat("b11??_????_????_????")
}

object OpCode2 {
  def LI = BitPat("b1000_0???_????_????")
  def Branch = BitPat("b1010_0???_????_????")
  def ConditionalBranch = BitPat("b1011_1???_????_????")
}

object BranchConditionCode {
  def BE = BitPat("b1011_1000_????_????")
  def BLT = BitPat("b1011_1001_????_????")
  def BLE = BitPat("b1011_1010_????_????")
  def BNE = BitPat("b1011_1011_????_????")
}

object OpCode3 {
  def ADD = BitPat("b11??_????_0000_????")
  def SUB = BitPat("b11??_????_0001_????")
  def AND = BitPat("b11??_????_0010_????")
  def OR  = BitPat("b11??_????_0011_????")
  def XOR = BitPat("b11??_????_0100_????")
  def CMP = BitPat("b11??_????_0101_????")
  def MOV = BitPat("b11??_????_0110_????")

  def SLL = BitPat("b11??_????_1000_????")
  def SLR = BitPat("b11??_????_1001_????")
  def SRL = BitPat("b11??_????_1010_????")
  def SRA = BitPat("b11??_????_1011_????")
  def IN  = BitPat("b11??_????_1100_????")
  def OUT = BitPat("b11??_????_1101_????")

  def HLT = BitPat("b11??_????_1111_????")
}

class InstructionDecoder extends Module {
  val io = IO(new Bundle{
    val phase = Input(UInt(2.W))
    val irValue = Input(UInt(16.W))
    val pcPlus1Value = Input(UInt(16.W))

    val registerWriteEnabled = Output(Bool())
    val registerWrite = Output(UInt(3.W))

    val registerRead0 = Output(UInt(3.W))
    val registerRead1 = Output(UInt(3.W))
    val registerValue0 = Input(SInt(16.W))
    val registerValue1 = Input(SInt(16.W))

    val ar = Output(SInt(16.W))
    val br = Output(SInt(16.W))
    val aluControl = Output(Bool())

    val branchCondition = Output(UInt(3.W))
  })

  val registerWriteEnabled = RegInit(0.U(1.W))
  val registerWrite = RegInit(0.U(3.W))
  val ar = RegInit(0.S(16.W))
  val br = RegInit(0.S(16.W))
  val aluControl = RegInit(0.U(1.W))
  val branchCondition = RegInit(0.U(3.W))

  io.registerWriteEnabled := registerWriteEnabled
  io.registerWrite := registerWrite
  io.registerRead0 := 0.U
  io.registerRead1 := 0.U
  io.ar := ar
  io.br := br
  io.aluControl := aluControl
  io.branchCondition := branchCondition

  when(io.phase === FourPhase.InstructionDecode){
    registerWriteEnabled := 0.U
    branchCondition := BranchCondition.Never.value.U
    when(io.irValue === OpCode1.Branch){
      when(io.irValue === OpCode2.LI){
        registerWriteEnabled := 1.U
        registerWrite := io.irValue(10, 8)
        ar := 0.S
        br := io.irValue(7, 0).asSInt
        aluControl := ALUOpcode.ADD.value.U
      }
      when(io.irValue === OpCode2.Branch){
        registerWriteEnabled := 0.U
        ar := io.pcPlus1Value.asSInt
        br := io.irValue(7, 0).asSInt
        aluControl := ALUOpcode.ADD.value.U
        branchCondition := BranchCondition.Always.value.U
      }
      when(io.irValue === OpCode2.ConditionalBranch){
        when(io.irValue === BranchConditionCode.BE){
          registerWriteEnabled := 0.U
          ar := io.pcPlus1Value.asSInt
          br := io.irValue(7, 0).asSInt
          aluControl := ALUOpcode.ADD.value.U
          branchCondition := BranchCondition.IfZ.value.U
        }
        when(io.irValue === BranchConditionCode.BLT){
          registerWriteEnabled := 0.U
          ar := io.pcPlus1Value.asSInt
          br := io.irValue(7, 0).asSInt
          aluControl := ALUOpcode.ADD.value.U
          branchCondition := BranchCondition.IfS.value.U
        }
        when(io.irValue === BranchConditionCode.BLE){
          registerWriteEnabled := 0.U
          ar := io.pcPlus1Value.asSInt
          br := io.irValue(7, 0).asSInt
          aluControl := ALUOpcode.ADD.value.U
          branchCondition := BranchCondition.IfSorZ.value.U
        }
        when(io.irValue === BranchConditionCode.BNE){
          registerWriteEnabled := 0.U
          ar := io.pcPlus1Value.asSInt
          br := io.irValue(7, 0).asSInt
          aluControl := ALUOpcode.ADD.value.U
          branchCondition := BranchCondition.IfNotZ.value.U
        }
      }
    }
    when(io.irValue === OpCode1.ComputeIO){
      when(io.irValue === OpCode3.ADD){
        registerWriteEnabled := 1.U
        registerWrite := io.irValue(10, 8)
        io.registerRead0 := io.irValue(10, 8)
        io.registerRead1 := io.irValue(13, 11)
        ar := io.registerValue0
        br := io.registerValue1
        aluControl := ALUOpcode.ADD.value.U
      }
      when(io.irValue === OpCode3.SUB){
        registerWriteEnabled := 1.U
        registerWrite := io.irValue(10, 8)
        io.registerRead0 := io.irValue(10, 8)
        io.registerRead1 := io.irValue(13, 11)
        ar := io.registerValue0
        br := io.registerValue1
        aluControl := ALUOpcode.SUB.value.U
      }
      when(io.irValue === OpCode3.MOV){
        registerWriteEnabled := 1.U
        registerWrite := io.irValue(10, 8)
        io.registerRead1 := io.irValue(13, 11)
        ar := 0.S
        br := io.registerValue1
        aluControl := ALUOpcode.ADD.value.U
      }
    }
  }
}

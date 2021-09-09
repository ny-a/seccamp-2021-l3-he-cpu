import chisel3._
import chisel3.util.BitPat

object ALUOpcode {
  def ADD = BitPat("b000")
  def SUB = BitPat("b001")
  def AND = BitPat("b010")
  def OR = BitPat("b011")
  def XOR = BitPat("b100")
}

class ALU16bit extends Module {
  val io = IO(new Bundle{
    val phase  = Input(UInt(2.W))
    val in0    = Input(SInt(16.W))
    val in1    = Input(SInt(16.W))
    val opcode = Input(UInt(3.W))
    val dr     = Output(SInt(16.W))
    val flagZ  = Output(Bool())
    val flagS  = Output(Bool())
  })

  val dr = RegInit(0.S(16.W))
  val flagZ = RegInit(0.U(1.W))
  val flagS = RegInit(0.U(1.W))

  dr := dr
  io.dr := dr
  io.flagZ := flagZ
  io.flagS := flagS

  when(io.phase === FourPhase.Execution){
    when(io.opcode === ALUOpcode.ADD){
      val result: SInt = io.in0 + io.in1
      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }

    when(io.opcode === ALUOpcode.SUB){
      val result: SInt = io.in0 - io.in1
      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }

    when(io.opcode === ALUOpcode.AND){
      val result: SInt = (io.in0.asUInt & io.in1.asUInt).asSInt
      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }

    when(io.opcode === ALUOpcode.OR){
      val result: SInt = (io.in0.asUInt | io.in1.asUInt).asSInt
      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }

    when(io.opcode === ALUOpcode.XOR){
      val result: SInt = (io.in0.asUInt ^ io.in1.asUInt).asSInt
      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }
  }
}

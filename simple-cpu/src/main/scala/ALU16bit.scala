import chisel3._
import chisel3.util.{BitPat, Cat}

object ALUOpcode {
  def ADD = BitPat("b0000")
  def SUB = BitPat("b0001")
  def AND = BitPat("b0010")
  def OR = BitPat("b0011")
  def XOR = BitPat("b0100")
  def SLL = BitPat("b0101")
  def SLR = BitPat("b0110")
  def SRL = BitPat("b0111")
  def SRA = BitPat("b1000")
}

class ALU16bit extends Module {
  val io = IO(new Bundle{
    val phase  = Input(UInt(2.W))
    val in0    = Input(SInt(16.W))
    val in1    = Input(SInt(16.W))
    val opcode = Input(UInt(4.W))
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

    when(io.opcode === ALUOpcode.SLL){
      val result: SInt = (io.in0.asUInt << io.in1(3, 0).asUInt).asSInt
      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }

    when(io.opcode === ALUOpcode.SLR){
      var result: SInt = 0.S

      when(io.in1 === 0.S){
        result = io.in0
      }
      when(io.in1 === 1.S){
        result = Cat(io.in0(0), io.in0(15, 1)).asSInt
      }
      when(io.in1 === 2.S){
        result = Cat(io.in0(1, 0), io.in0(15, 2)).asSInt
      }
      when(io.in1 === 3.S){
        result = Cat(io.in0(2, 0), io.in0(15, 3)).asSInt
      }
      when(io.in1 === 4.S){
        result = Cat(io.in0(3, 0), io.in0(15, 4)).asSInt
      }
      when(io.in1 === 5.S){
        result = Cat(io.in0(4, 0), io.in0(15, 5)).asSInt
      }
      when(io.in1 === 6.S){
        result = Cat(io.in0(5, 0), io.in0(15, 6)).asSInt
      }
      when(io.in1 === 7.S){
        result = Cat(io.in0(6, 0), io.in0(15, 7)).asSInt
      }
      when(io.in1 === 8.S){
        result = Cat(io.in0(7, 0), io.in0(15, 8)).asSInt
      }
      when(io.in1 === 9.S){
        result = Cat(io.in0(8, 0), io.in0(15, 9)).asSInt
      }
      when(io.in1 === 10.S){
        result = Cat(io.in0(9, 0), io.in0(15, 10)).asSInt
      }
      when(io.in1 === 11.S){
        result = Cat(io.in0(10, 0), io.in0(15, 11)).asSInt
      }
      when(io.in1 === 12.S){
        result = Cat(io.in0(11, 0), io.in0(15, 12)).asSInt
      }
      when(io.in1 === 13.S){
        result = Cat(io.in0(12, 0), io.in0(15, 13)).asSInt
      }
      when(io.in1 === 14.S){
        result = Cat(io.in0(13, 0), io.in0(15, 14)).asSInt
      }
      when(io.in1 === 15.S){
        result = Cat(io.in0(14, 0), io.in0(15)).asSInt
      }

      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }

    when(io.opcode === ALUOpcode.SRL){
      val result: SInt = (io.in0.asUInt >> io.in1(3, 0).asUInt).asSInt
      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }

    when(io.opcode === ALUOpcode.SRA){
      val result: SInt = (io.in0 >> io.in1(3, 0).asUInt).asSInt
      dr := result
      flagZ := result === 0.S
      flagS := result < 0.S
    }
  }
}

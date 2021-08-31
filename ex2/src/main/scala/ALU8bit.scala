import chisel3._
import chisel3.util.BitPat

object ALUOpcode {
  def ADD = BitPat("b000")
  def SUB = BitPat("b001")
  def AND = BitPat("b010")
  def XOR = BitPat("b011")
  def OR  = BitPat("b100")
}

class ALU16bit extends Module {
    val io = IO(new Bundle{
        val inA    = Input(UInt(16.W))
        val inB    = Input(UInt(16.W))
        val opcode = Input(UInt(3.W))
        val out    = Output(UInt(16.W))
    })

    io.out := DontCare

    when(io.opcode === ALUOpcode.ADD){
        io.out := io.inA + io.inB
    }

    when(io.opcode === ALUOpcode.SUB){
        io.out := io.inA - io.inB
    }

    when(io.opcode === ALUOpcode.AND){
        io.out := io.inA & io.inB
    }

    when(io.opcode === ALUOpcode.XOR){
        io.out := io.inA ^ io.inB
    }

    when(io.opcode === ALUOpcode.OR){
        io.out := io.inA | io.inB
    }
}

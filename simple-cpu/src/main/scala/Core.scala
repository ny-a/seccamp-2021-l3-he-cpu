import chisel3._

class Core extends Module {
  val io = IO(new Bundle{
    val romAddr = Output(UInt(16.W))
    val romData = Input(UInt(16.W))

    val out = Output(SInt(16.W))
  })

  val phaseCounter = Module(new FourPhaseCounter())
  val instructionFetcher = Module(new InstructionFetcher())
  val instructionDecoder = Module(new InstructionDecoder())
  val registerFile = Module(new RegisterFile())
  val alu = Module(new ALU16bit())
  val branchController = Module(new BranchController())

  // DEBUG
  io.out := registerFile.io.out0

  // phase control
  instructionFetcher.io.phase := phaseCounter.io.phase
  instructionDecoder.io.phase := phaseCounter.io.phase
  registerFile.io.phase := phaseCounter.io.phase
  alu.io.phase := phaseCounter.io.phase
  branchController.io.phase := phaseCounter.io.phase

  // Instruction Fetch phase
  instructionFetcher.io.isBranching := branchController.io.isBranching
  instructionFetcher.io.drValue := alu.io.dr

  io.romAddr := instructionFetcher.io.romAddr
  instructionFetcher.io.romData := io.romData

  // Instruction Decode phase
  instructionDecoder.io.irValue := instructionFetcher.io.ir
  instructionDecoder.io.pcPlus1Value := instructionFetcher.io.pcPlus1

  registerFile.io.writeEnabled := instructionDecoder.io.registerWriteEnabled
  registerFile.io.writeSelect := instructionDecoder.io.registerWrite

  registerFile.io.readSelect0 := instructionDecoder.io.registerRead0
  registerFile.io.readSelect1 := instructionDecoder.io.registerRead1
  instructionDecoder.io.registerValue0 := registerFile.io.out0
  instructionDecoder.io.registerValue1 := registerFile.io.out1

  // Execution phase
  alu.io.in0 := instructionDecoder.io.ar
  alu.io.in1 := instructionDecoder.io.br
  alu.io.opcode := instructionDecoder.io.aluControl

  branchController.io.branchCondition := instructionDecoder.io.branchCondition
  branchController.io.flagZ := alu.io.flagZ

  // Write Back phase
  registerFile.io.drValue := alu.io.dr
  instructionFetcher.io.drValue := alu.io.dr
}

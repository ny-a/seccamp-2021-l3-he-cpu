object Main extends App{
  chisel3.Driver.execute(args, () => new ALU8bit())
  chisel3.Driver.execute(args, () => new Sum8bit(9, 9))
}
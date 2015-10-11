require 'matrix'
class Matrix
  def to_readable
  	string = ''
    i = 0
    self.each do |number|
      i+= 1
      if i == self.column_size
      string+= number.to_s
        string+= "\n"
        i = 0
else 
      string+= number.to_s + ", "
      end
    end
    return string
  end
end
target = open('input.txt', 'w')

i=2
while i <=50 do
x=0
while x<2 do
m1 = Matrix.build(i,i){|row,column| (rand*1000).round(3)}
target.write(m1.to_readable)
target.write("\n")
x+=1
end 
i+=1
end
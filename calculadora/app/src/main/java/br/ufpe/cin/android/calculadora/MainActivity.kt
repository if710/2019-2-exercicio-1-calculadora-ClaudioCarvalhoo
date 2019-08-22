package br.ufpe.cin.android.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Para cada botão, faço com que ele adicione to text_calc seu valor ao final do valor que já está lá atualmente.
        btn_0.setOnClickListener{
            text_calc.append("0")
        }
        btn_1.setOnClickListener{
            text_calc.append("1")
        }
        btn_2.setOnClickListener{
            text_calc.append("2")
        }
        btn_3.setOnClickListener{
            text_calc.append("3")
        }
        btn_4.setOnClickListener{
            text_calc.append("4")
        }
        btn_5.setOnClickListener{
            text_calc.append("5")
        }
        btn_6.setOnClickListener{
            text_calc.append("6")
        }
        btn_7.setOnClickListener{
            text_calc.append("7")
        }
        btn_8.setOnClickListener{
            text_calc.append("8")
        }
        btn_9.setOnClickListener{
            text_calc.append("9")
        }
        btn_Dot.setOnClickListener{
            text_calc.append(".")
        }
        btn_Divide.setOnClickListener{
            text_calc.append("/")
        }
        btn_Multiply.setOnClickListener{
            text_calc.append("*")
        }
        btn_Subtract.setOnClickListener{
            text_calc.append("-")
        }
        btn_Add.setOnClickListener{
            text_calc.append("+")
        }
        btn_Power.setOnClickListener{
            text_calc.append("^")
        }
        btn_LParen.setOnClickListener{
            text_calc.append("(")
        }
        btn_RParen.setOnClickListener{
            text_calc.append(")")
        }
        //Para o botão de clear, troco o valor atual por uma string vazia.
        btn_Clear.setOnClickListener{
            text_calc.setText("")
        }

        //Quando o botão de = é pressionado, chama a função eval para o text que estiver no text_calc.
        btn_Equal.setOnClickListener{
            try {
                val result = eval(text_calc.text.toString())
                text_info.text = result.toString()
            }catch (e:java.lang.RuntimeException){
                //Caso a função eval falhe, crio um toast para avisar ao usuário que algo de errado não está certo.
                Toast.makeText(this, "Expressão inválida, vacilou.", Toast.LENGTH_SHORT).show()
            }catch (e:Exception){
                //Dado que a função de eval só joga explicitamente runtime exception, seria uma proeza entrar aqui. Mas, se o cara conseguir, ele toma um toast com mensagem diferenciada pela façanha.
                Toast.makeText(this, "Realmente nem sei como vc fez isso.", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Dou um override na função que salva o estado antes da atividade ser reiniciada, salvando os valores dos dois textos em um estado que prevalecerá.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("info", text_info.text.toString())
        outState.putString("calc", text_calc.text.toString())
    }

    //Dou um override na função que recupera o estado após a atividade ser reiniciada e, com ela, os valores que salvei na função acima, podendo assim recolocá-los onde eles estavam antes.
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        text_info.text = savedInstanceState.getString("info")
        text_calc.setText(savedInstanceState.getString("calc"))
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }
}

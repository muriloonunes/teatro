import java.util.*

fun validarCPF(cpf: Long): Boolean {
    val cpfString = String.format("%011d", cpf)
    if (cpf == 0L || cpf % 11111111111L == 0L) {
        return false
        // retorna false se o cpf digitado foi 000 ou se todos seus dígitos forem iguais (22222222222, etc)
    }

    val cpfArray = IntArray(11)
    for (i in 0..10) {
        cpfArray[i] = cpfString.get(i).toString().toInt()
        // passa os dígitos da string cpf para cada posiçao do array
    }

    var soma = 0
    var peso = 10
    for (i in 0..8) {
        soma += cpfArray[i] * peso
        peso--
    }
    val primeiroDigito = if (soma % 11 < 2) 0 else 11 - (soma % 11)

    // se o resto da divisão de soma por 11 for menor que 2, primeiroDigito recebe 0. caso contrario, primeiroDigito recebe 11 - soma % 11
    soma = 0
    peso = 11
    for (i in 0..9) {
        soma += cpfArray[i] * peso
        peso--
    }
    val segundoDigito = if (soma % 11 < 2) 0 else 11 - (soma % 11)

    // se o resto da divisão de soma por 11 for menor que 2, segundoDigito recebe 0. caso contrario, segundoDigito recebe 11 - soma % 11
    return cpfArray[9] == primeiroDigito && cpfArray[10] == segundoDigito
    // verifica se as variaveis primeiroDigito e segundoDigito sao iguais aos digitos verificadores do CPF, que estao na posicao 9 e 10 do array
}




//claudinho sugeriu

/**
 * Lê um inteiro do scanner, repetindo a pergunta até o usuário digitar algo válido.
 * Evita que o programa quebre (InputMismatchException) por entrada não numérica.
 */
fun lerInteiro(scanner: Scanner, mensagem: String): Int {
    while (true) {
        print(mensagem)
        val entrada = scanner.nextLine().trim()
        val valor = entrada.toIntOrNull()
        if (valor != null) {
            return valor
        }
        println("Entrada inválida. Digite um número inteiro.")
    }
}

/**
 * Igual a lerInteiro, mas só aceita valores dentro do intervalo [min, max].
 */
fun lerInteiroEmIntervalo(scanner: Scanner, mensagem: String, min: Int, max: Int): Int {
    while (true) {
        val valor = lerInteiro(scanner, mensagem)
        if (valor in min..max) {
            return valor
        }
        println("Valor fora do intervalo permitido ($min a $max).")
    }
}

/**
 * Lê uma linha de texto obrigatória (não pode ficar em branco).
 */
fun lerTextoNaoVazio(scanner: Scanner, mensagem: String): String {
    while (true) {
        print(mensagem)
        val entrada = scanner.nextLine().trim()
        if (entrada.isNotEmpty()) {
            return entrada
        }
        println("Este campo não pode ficar em branco.")
    }
}

/**
 * Lê uma linha de texto opcional (campos secundários do cadastro de fidelidade).
 */
fun lerTexto(scanner: Scanner, mensagem: String): String {
    print(mensagem)
    return scanner.nextLine().trim()
}

/**
 * Lê e valida um CPF: exige 11 dígitos numéricos e checa os dígitos verificadores.
 * Digitar "0" cancela a operação (retorna null).
 */
fun lerCPF(scanner: Scanner, mensagem: String = "Digite o CPF (11 dígitos, ou 0 para cancelar): "): String? {
    while (true) {
        print(mensagem)
        val entrada = scanner.nextLine().trim()

        if (entrada == "0") {
            return null
        }

        if (entrada.length != 11 || !entrada.all { it.isDigit() }) {
            println("CPF inválido. Digite exatamente 11 números, sem pontos ou traços.")
            continue
        }

        val cpfLong = entrada.toLongOrNull()
        if (cpfLong == null || !validarCPF(cpfLong)) {
            println("CPF inválido (dígitos verificadores não conferem).")
            continue
        }

        return entrada
    }
}

/**
 * Lê e valida uma data no formato dd/mm/aaaa (formato, mês válido, e dia real do mês/ano,
 * considerando fevereiro em ano bissexto).
 */
fun lerDataNascimento(scanner: Scanner): String {
    val formato = Regex("""^(\d{2})/(\d{2})/(\d{4})$""")
    while (true) {
        print("Data de Nascimento (dd/mm/aaaa): ")
        val entrada = scanner.nextLine().trim()
        val match = formato.matchEntire(entrada)

        if (match == null) {
            println("Formato inválido. Use dd/mm/aaaa.")
            continue
        }

        val dia = match.groupValues[1].toInt()
        val mes = match.groupValues[2].toInt()
        val ano = match.groupValues[3].toInt()

        if (!dataValida(dia, mes, ano)) {
            println("Data inválida.")
            continue
        }

        return entrada
    }
}

fun dataValida(dia: Int, mes: Int, ano: Int): Boolean {
    if (mes !in 1..12) return false
    val anoAtual = Calendar.getInstance().get(Calendar.YEAR)
    if (ano !in 1900..anoAtual) return false

    val diasNoMes = when (mes) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (anoBissexto(ano)) 29 else 28
        else -> 0
    }
    return dia in 1..diasNoMes
}

fun anoBissexto(ano: Int): Boolean {
    return (ano % 4 == 0 && ano % 100 != 0) || (ano % 400 == 0)
}
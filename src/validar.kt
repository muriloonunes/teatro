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
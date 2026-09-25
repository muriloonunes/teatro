import java.util.*

val AREA_NOMES = arrayOf("Plateia A", "Plateia B", "Frisa", "Camarote", "Balcão Nobre")
val AREA_PREFIXOS = arrayOf("PA", "PB", "FR", "CM", "BN")
val AREA_PRECOS = doubleArrayOf(
    PRECO_PLATEIA_A,
    PRECO_PLATEIA_B,
    PRECO_FRISA,
    PRECO_CAMAROTE,
    PRECO_BALCAO
)
val AREA_CAPACIDADES = intArrayOf(
    ASSENTOS_PLATEIA_A,
    ASSENTOS_PLATEIA_B,
    NUM_FRISAS,
    NUM_CAMAROTES,
    ASSENTOS_BALCAO
)

val mapaPlateiaA = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(ASSENTOS_PLATEIA_A) } }
val mapaPlateiaB = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(ASSENTOS_PLATEIA_B) } }
val mapaFrisas = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(NUM_FRISAS) } }
val mapaCamarotes = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(NUM_CAMAROTES) } }
val mapaBalcao = Array(NUM_PECAS) { Array(NUM_SESSOES) { BooleanArray(ASSENTOS_BALCAO) } }

val clientesCpf = mutableListOf<String>()
val clientesNome = mutableListOf<String>()
val clientesTelefone = mutableListOf<String>()
val clientesDataNascimento = mutableListOf<String>()
val clientesCidade = mutableListOf<String>()
val clientesEstado = mutableListOf<String>()
val clientesFidelidade = mutableListOf<Boolean>()

val ingressosPecaIndex = mutableListOf<Int>()
val ingressosSessaoIndex = mutableListOf<Int>()
val ingressosAreaIndex = mutableListOf<Int>()
val ingressosCodigoAssento = mutableListOf<String>()
val ingressosPreco = mutableListOf<Double>()
val ingressosCpfCliente = mutableListOf<String>()
val ingressosFormaPagamento = mutableListOf<String>()

fun main() {
    val scanner = Scanner(System.`in`)
    var opcao: Int

    do {
        println("\n=== Bem vindo ao Teatro ABC ===")
        println("1. Comprar Ingresso")
        println("2. Estatísticas")
        println("3. Imprimir Ingresso por CPF")
        println("0. Sair")
        opcao = lerInteiroEmIntervalo(scanner, "Escolha uma opção: ", 0, 3)

        when (opcao) {
            1 -> comprarIngresso(scanner)
            2 -> estatisticas()
            3 -> imprimirIngresso(scanner)
            0 -> println("Encerrando o sistema...")
        }
    } while (opcao != 0)
}

fun comprarIngresso(scanner: Scanner) {
    println("\nPara qual peça deseja comprar o ingresso?")
    for (i in NOMES_PECAS.indices) {
        println("${i + 1}.${NOMES_PECAS[i]}")
    }
    println("0. Voltar")
    val pecaEscolhida = lerInteiroEmIntervalo(scanner, "Escolha: ", 0, NUM_PECAS)
    if (pecaEscolhida == 0) return

    println("\nPara qual horário deseja comprar o ingresso?")
    for (i in NOMES_SESSOES.indices) {
        println("${i + 1}.${NOMES_SESSOES[i]}")
    }
    println("0. Escolher outra peça")
    val sessaoEscolhida = lerInteiroEmIntervalo(scanner, "Escolha: ", 0, NUM_SESSOES)
    if (sessaoEscolhida == 0) return

    val pecaIndex = pecaEscolhida - 1
    val sessaoIndex = sessaoEscolhida - 1

    menuAreas(scanner, pecaIndex, sessaoIndex)
}

fun menuAreas(scanner: Scanner, pecaIndex: Int, sessaoIndex: Int) {
    println("\n--- SELEÇÃO DE ÁREA DO TEATRO (${NOMES_PECAS[pecaIndex]} -${NOMES_SESSOES[sessaoIndex]}) ---")

    for (i in 0..<NUM_AREAS) {
        println("${i + 1}.${AREA_NOMES[i]} (R$ ${"%.2f".format(AREA_PRECOS[i])})")
    }
    println("0. Cancelar")
    val setorEscolhido = lerInteiroEmIntervalo(scanner, "Escolha o setor: ", 0, NUM_AREAS)
    if (setorEscolhido == 0) return

    val areaIndex = setorEscolhido - 1
    val mapa = obterMapa(areaIndex, pecaIndex, sessaoIndex)

    selecionarAssento(scanner, pecaIndex, sessaoIndex, areaIndex, mapa)
}

fun obterMapa(areaIndex: Int, pecaIndex: Int, sessaoIndex: Int): BooleanArray {
    return when (areaIndex) {
        0 -> mapaPlateiaA[pecaIndex][sessaoIndex]
        1 -> mapaPlateiaB[pecaIndex][sessaoIndex]
        2 -> mapaFrisas[pecaIndex][sessaoIndex]
        3 -> mapaCamarotes[pecaIndex][sessaoIndex]
        4 -> mapaBalcao[pecaIndex][sessaoIndex]
        else -> throw IllegalArgumentException("Área inválida: $areaIndex")
    }
}

fun selecionarAssento(scanner: Scanner, pecaIndex: Int, sessaoIndex: Int, areaIndex: Int, mapa: BooleanArray) {
    val nomeArea = AREA_NOMES[areaIndex]
    val prefixoArea = AREA_PREFIXOS[areaIndex]
    val capacidadeArea = AREA_CAPACIDADES[areaIndex]

    println("\nMapa de Assentos - $nomeArea ([ ]Livre, [X]Ocupado):")
    for (i in mapa.indices) {
        val status = if (mapa[i]) "X" else " "
        print(String.format("%s%02d:[%s]  ", prefixoArea, i + 1, status))
        if ((i + 1) % 10 == 0) println()
    }
    println()

    if (mapa.all { it }) {
        println("Todos os assentos desta área estão ocupados para esta sessão.")
        return
    }

    val numero = lerInteiroEmIntervalo(
        scanner,
        "Digite o número do assento desejado (1 a $capacidadeArea, 0 para cancelar): ",
        0,
        capacidadeArea
    )
    if (numero == 0) return

    val assentoIdx = numero - 1

    if (mapa[assentoIdx]) {
        println("Assento já ocupado! Escolha outro.")
        selecionarAssento(scanner, pecaIndex, sessaoIndex, areaIndex, mapa)
        return
    }

    val codigoAssento = String.format("%s%02d", prefixoArea, numero)
    finalizarCheckout(scanner, pecaIndex, sessaoIndex, areaIndex, codigoAssento, mapa, assentoIdx)
}

fun finalizarCheckout(
    scanner: Scanner,
    pecaIndex: Int,
    sessaoIndex: Int,
    areaIndex: Int,
    codigoAssento: String,
    mapa: BooleanArray,
    assentoIdx: Int
) {
    println("\n--- FINALIZAR COMPRA ---")
    val cpf = lerCPF(scanner) ?: run {
        println("Compra cancelada.")
        return
    }

    val indiceCliente = clientesCpf.indexOf(cpf)

    if (indiceCliente == -1) {
        println("Cadastro não encontrado para este CPF.")
        val opcFidelidade =
            lerInteiroEmIntervalo(scanner, "Deseja realizar o cadastro completo? (1. Sim / 2. Não): ", 1, 2)

        var nome = ""
        var telefone = ""
        var dataNascimento = ""
        var cidade = ""
        var estado = ""
        var fidelidade = false

        if (opcFidelidade == 1) {
            fidelidade = true
            nome = lerTextoNaoVazio(scanner, "Nome completo: ")
            telefone = lerTextoNaoVazio(scanner, "Telefone: ")
            dataNascimento = lerDataNascimento(scanner)
            cidade = lerTexto(scanner, "Cidade: ")
            estado = lerTexto(scanner, "Estado: ")
        }

        clientesCpf.add(cpf)
        clientesNome.add(nome)
        clientesTelefone.add(telefone)
        clientesDataNascimento.add(dataNascimento)
        clientesCidade.add(cidade)
        clientesEstado.add(estado)
        clientesFidelidade.add(fidelidade)
    }

    println("\nForma de pagamento:")
    println("1. Cartão de Crédito\n2. Cartão de Débito\n3. Boleto\n4. PIX")
    val opcaoPagamento = lerInteiroEmIntervalo(scanner, "Escolha a opção: ", 1, 4)
    val formaPagto = when (opcaoPagamento) {
        1 -> "Crédito"
        2 -> "Débito"
        3 -> "Boleto"
        else -> "PIX"
    }

    mapa[assentoIdx] = true

    ingressosPecaIndex.add(pecaIndex)
    ingressosSessaoIndex.add(sessaoIndex)
    ingressosAreaIndex.add(areaIndex)
    ingressosCodigoAssento.add(codigoAssento)
    ingressosPreco.add(AREA_PRECOS[areaIndex])
    ingressosCpfCliente.add(cpf)
    ingressosFormaPagamento.add(formaPagto)

    println("\nCompra realizada com sucesso. Assento: $codigoAssento (${AREA_NOMES[areaIndex]})!")
}

fun estatisticas() {
    val totalIngressos = ingressosCodigoAssento.size
    if (totalIngressos == 0) {
        println("\nNenhuma venda registrada ainda.")
        return
    }

    val vendasPorPeca = IntArray(NUM_PECAS)
    val lucroPorPeca = DoubleArray(NUM_PECAS)
    val vendasPorSessao = IntArray(NUM_SESSOES)
    val lucroPorPecaSessao = Array(NUM_PECAS) { DoubleArray(NUM_SESSOES) }
    val vendasPorArea = IntArray(NUM_AREAS)

    for (i in 0..<totalIngressos) {
        val pIndex = ingressosPecaIndex[i]
        val sIndex = ingressosSessaoIndex[i]
        val aIndex = ingressosAreaIndex[i]
        val preco = ingressosPreco[i]

        vendasPorPeca[pIndex]++
        lucroPorPeca[pIndex] += preco
        vendasPorSessao[sIndex]++
        lucroPorPecaSessao[pIndex][sIndex] += preco
        vendasPorArea[aIndex]++
    }

    val pecaMaisVendida = indiceMaiorValor(vendasPorPeca)
    val pecaMenosVendida = indiceMenorValor(vendasPorPeca)
    val sessaoMaisOcupada = indiceMaiorValor(vendasPorSessao)
    val sessaoMenosOcupada = indiceMenorValor(vendasPorSessao)

    println("\n--- ESTATÍSTICAS DE VENDAS ---")
    println("Total de ingressos vendidos: $totalIngressos")
    println("Peça mais vendida: ${NOMES_PECAS[pecaMaisVendida]} (${vendasPorPeca[pecaMaisVendida]} ingressos)")
    println("Peça menos vendida: ${NOMES_PECAS[pecaMenosVendida]} (${vendasPorPeca[pecaMenosVendida]} ingressos)")
    println("Sessão mais ocupada: ${NOMES_SESSOES[sessaoMaisOcupada]}")
    println("Sessão menos ocupada: ${NOMES_SESSOES[sessaoMenosOcupada]}")

    println("\nDetalhamento por peça:")
    for (i in 0..<NUM_PECAS) {
        val lucroMedio = if (vendasPorPeca[i] != 0) lucroPorPeca[i] / vendasPorPeca[i] else 0.0
        val sessaoMaisLucrativa = indiceMaiorValor(lucroPorPecaSessao[i])
        val sessaoMenosLucrativa = indiceMenorValor(lucroPorPecaSessao[i])
        println(
            "- ${NOMES_PECAS[i]}:${vendasPorPeca[i]} vendidos, faturamento R$ ${"%.2f".format(lucroPorPeca[i])}, ticket médio R$ ${
                "%.2f".format(
                    lucroMedio
                )
            }"
        )
        println("  Sessão mais lucrativa: ${NOMES_SESSOES[sessaoMaisLucrativa]} | Sessão menos lucrativa:${NOMES_SESSOES[sessaoMenosLucrativa]}")
    }

    println("\nVendas por área:")
    for (i in 0..<NUM_AREAS) {
        println("- ${AREA_NOMES[i]}:${vendasPorArea[i]} ingressos")
    }
}

fun imprimirIngresso(scanner: Scanner) {
    println("\n--- IMPRIMIR INGRESSO ---")
    val cpf = lerCPF(scanner) ?: run {
        println("Operação cancelada.")
        return
    }

    val indicesEncontrados = mutableListOf<Int>()
    for (i in ingressosCpfCliente.indices) {
        if (ingressosCpfCliente[i] == cpf) {
            indicesEncontrados.add(i)
        }
    }

    if (indicesEncontrados.isEmpty()) {
        println("Nenhum ingresso encontrado para o CPF informado.")
        return
    }

    val clienteIndex = clientesCpf.indexOf(cpf)
    val nomeCliente = if (clienteIndex != -1) clientesNome[clienteIndex].takeIf { it.isNotBlank() } else null
    val ehFidelidade = clienteIndex != -1 && clientesFidelidade[clienteIndex]

    println(if (nomeCliente != null) "\nIngressos de $nomeCliente:" else "\nIngressos encontrados:")
    for (i in indicesEncontrados) {
        val pecaIdx = ingressosPecaIndex[i]
        val sessaoIdx = ingressosSessaoIndex[i]
        val areaIdx = ingressosAreaIndex[i]
        if (ehFidelidade) println("Cliente do programa de fidelidade")

        println("----------------------------------")
        println("Peça: ${NOMES_PECAS[pecaIdx]}")
        println("Sessão: ${NOMES_SESSOES[sessaoIdx]}")
        println("Área: ${AREA_NOMES[areaIdx]}")
        println("Assento: ${ingressosCodigoAssento[i]}")
        println("Preço: R$ ${"%.2f".format(ingressosPreco[i])}")
        println("Forma de pagamento: ${ingressosFormaPagamento[i]}")
    }
    println("----------------------------------")
}

fun indiceMaiorValor(valores: IntArray): Int {
    var maior = 0
    for (i in 1..<valores.size) if (valores[i] > valores[maior]) maior = i
    return maior
}

fun indiceMenorValor(valores: IntArray): Int {
    var menor = 0
    for (i in 1..<valores.size) if (valores[i] < valores[menor]) menor = i
    return menor
}

fun indiceMaiorValor(valores: DoubleArray): Int {
    var maior = 0
    for (i in 1..<valores.size) if (valores[i] > valores[maior]) maior = i
    return maior
}

fun indiceMenorValor(valores: DoubleArray): Int {
    var menor = 0
    for (i in 1..<valores.size) if (valores[i] < valores[menor]) menor = i
    return menor
}
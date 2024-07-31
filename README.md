# Projeto de Decodificação de Código de Barras

## Descrição do Projeto

Este projeto é uma aplicação web em Java com Spring Boot para decodificação de códigos de barras de imagens ou arquivos PDF. Ele permite que os usuários façam upload de arquivos contendo códigos de barras, que são então processados para extrair informações dos mesmos.

## Funcionalidades

- **Upload de Arquivos**: Permite o upload de imagens ou arquivos PDF contendo códigos de barras.
- **Decodificação de Códigos de Barras**: Suporta uma ampla variedade de formatos de códigos de barras, incluindo Code 128, EAN-13, UPC-A, e muitos outros.
- **Resposta com Resultados**: Retorna o texto decodificado dos códigos de barras encontrados ou uma mensagem de erro caso nenhum código de barras seja detectado.

## Requisitos

- **Java 17 ou superior**
- **Spring Boot 3.3.2 ou superior**
- **Bibliotecas adicionais**: 
  - `ZXing` para decodificação de códigos de barras
  - `PDFBox` para manipulação de arquivos PDF


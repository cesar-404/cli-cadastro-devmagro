import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Menu {
    Scanner scan = new Scanner(System.in);
    private final String FORM_PATH = "static/form.txt";
    ArrayList<User> users = new ArrayList<>();
    ArrayList<String> questions = new ArrayList<>();
    int userIndex = 1;
    int questionsIndex = 5;

    public void app() {
        printPrincipalMenu();

        int choice = -1;
        while (choice < 0 || choice > 5) {
            if (scan.hasNextInt()) {
                choice = scan.nextInt();
                scan.nextLine();
            } else {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                scan.nextLine();
            }
        }

        switch (choice) {
            case 1:
                cadUser();
                app();
                break;
            case 2:
                listAllUsers();
                app();
                break;
            case 3:
                cadNewQuestion();
                app();
                break;
            case 4:
                deleteQuestion();
                app();
                break;
            case 5:
                searchUser();
                app();
                break;
            case 0:
                System.out.println("Encerrando programa....");
                System.exit(0);
                break;
        }
    }

    public void printPrincipalMenu() {
        String PATH_MENU = "static/principalmenu.txt";
        ArrayList<String> menu = reader(PATH_MENU);
        for (String line: menu) {
            System.out.println(line);
        }

    }

    public void printCadMenu() {
        ArrayList<String> menu = reader(FORM_PATH);
        for (String line: menu) {
            System.out.println(line);
        }
    }

    public void cadUser() {
        printCadMenu();

        String name = scan.nextLine();
        while (name.length() < 10) {
            System.out.println("O nome deve possuir no mínimo 10 caracteres. Tente novamente.");
            name = scan.nextLine();
        }

        String email = scan.nextLine();
        while (!email.contains("@")) {
            System.out.println("O email deve conter \"@\". Tente novamente.");
            email = scan.nextLine();
        }
        while (true) {
            boolean emailExistente = false;

            for (User user : users) {
                if (user.getEmail().equals(email)) {
                    emailExistente = true;
                    break;
                }
            }

            if (emailExistente) {
                System.out.println("O email já está cadastrado. Tente novamente.");
            } else {
                break;
            }
        }

        int age = Integer.parseInt(scan.nextLine());
        if (age < 18) {
            System.out.println("O usuário deve ter idade maior que 18 anos.");
            System.out.println("Encerrando o programa.");
            System.exit(0);
        }

        double height = 0.0;
        boolean valid = false;
        while (!valid) {
            String inputHeight = scan.nextLine();
            if (inputHeight.matches("^\\d+,\\d+$")) {
                inputHeight = inputHeight.replace(",", ".");
                try {
                    height = Double.parseDouble(inputHeight);
                    valid = true;
                } catch (NumberFormatException e) {
                    System.out.println("Erro " + e.getMessage());
                }
            } else {
                System.out.println("Entrada inválidade. Certifique-se de usar vírgula.");
            }
        }

        User user = new User(name, email, age, height);
        users.add(user);
        writer(user);

        System.out.println("Usuário cadastrado com sucesso.");
        System.out.println("Informações do usuário");
        System.out.println(user);

    }

    public void listAllUsers() {
        for(User user: users) {
            System.out.println(user.getName());
        }
    }

    public void cadNewQuestion() {
        questions = reader(FORM_PATH);
        System.out.println("Perguntas cadastradas:");
        printCadMenu();

        System.out.println("Digite um nova pergunta:");
        String newQuestion = scan.nextLine();

        writer(newQuestion);
    }

    public void deleteQuestion() {
        System.out.println("Perguntas cadastradas:");
        printCadMenu();
        questions = reader(FORM_PATH);

        int questionIndex = scan.nextInt() - 1;
        scan.nextLine();
        if (questionIndex < 4 || questionIndex > questions.size()) {
            System.out.println("Index inválido.\nTente novamente.");
            deleteQuestion();
        }

        questions.remove(questionIndex);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FORM_PATH))) {
            for (int i = 0; i < questions.size(); i++) {
                String question = questions.get(i)
                        .replaceAll("-", "")
                        .replaceAll("\\d+", "")
                        .replaceAll("\\s{2,}", " ")
                        .trim();

                bw.write((i + 1) + " - " + question);
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }

        System.out.println("Pergunta deletada com sucesso.");
    }

    public void searchUser() {
        System.out.println("Digite o nome, email ou idade do usuário:");
        String userSearched = scan.nextLine();

        if (userSearched.isEmpty()) {
            return;
        }

        boolean found = false;

        try {
            int idadeBuscada = Integer.parseInt(userSearched);

            for (User user : users) {
                if (user.getAge() == idadeBuscada) {
                    System.out.println(user.getName());
                    found = true;
                }
            }
        } catch (NumberFormatException e) {
            for (User user : users) {
                if (user.getName().equalsIgnoreCase(userSearched) ||
                        user.getEmail().equalsIgnoreCase(userSearched)) {
                    System.out.println(user.getName());
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("Usuário não está cadastrado.");
        }
    }

    public ArrayList<String> reader(String path) {
        ArrayList<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(Paths.get(path))) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);
            }

        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
        }

        return lines;
    }

    public void writer(User user) {

        String userPath = "static/users/" + userIndex + "-" + user.getName().toUpperCase().replaceAll(" ", "") + ".txt";

        try (OutputStream os = new FileOutputStream(userPath);
             Writer wr = new OutputStreamWriter(os);
             BufferedWriter br = new BufferedWriter(wr)) {

             br.write(user.toString());

        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
        userIndex++;
    }

    public void writer(String question) {
        String newQuestionFormated = questionsIndex + " - " + question;

        try (OutputStream os = new FileOutputStream(FORM_PATH, true);
             Writer wr = new OutputStreamWriter(os);
             BufferedWriter br = new BufferedWriter(wr)) {

            br.newLine();
            br.write(newQuestionFormated);

        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }

        questionsIndex++;
    }
}
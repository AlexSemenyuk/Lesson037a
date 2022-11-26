package org.itstep;

import java.io.*;
import java.util.*;

public class Base {
    final static Scanner scanner = new Scanner(System.in);
    final static Scanner scanner1 = new Scanner(System.in);
    private List<User> users;

    public Base() throws IOException {
        this(new ArrayList<>());
    }

    public Base(List<User> users) throws IOException {
        this.users = users;
    }


    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Base{" +
                "users=" + users +
                '}';
    }

    public static Base createBaseFromFile(String fileUsers, String fileTasks) throws IOException {
        Base rezult = new Base();
        List<User> userList;
        List<Task> taskList;


        // file users
        String stUsers = "";

        try (InputStream inUsers = new FileInputStream(fileUsers);
             DataInputStream dataInUsers = new DataInputStream(inUsers)) {
            while (dataInUsers.available() > 0) {
                stUsers += dataInUsers.readUTF();
            }
            String[] arrUsers = stUsers.split("\\n");
            userList = Arrays.stream(arrUsers)
                    .map(s -> s.split(","))
                    .map(arr -> new String[]{String.valueOf(arr[0]), String.valueOf(arr[1]), String.valueOf(arr[2])})
                    .map(arr -> new User(arr[0], arr[1], arr[2]))
                    .toList();
            if (stUsers.length() > 0) {
                for (User user : userList) {
                    rezult.getUsers().add(user);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // file taskList
        String[] arrTasks;
        Task tmpTask;
        String stTasks = "";

        try (InputStream inTasks = new FileInputStream(fileTasks);
        DataInputStream dataInTasks = new DataInputStream(inTasks)) {
            while (dataInTasks.available() > 0) {
                stTasks += dataInTasks.readUTF();
            }
            String[] stringArr1 = stTasks.split("\\n");

            if (stringArr1.length > 0) {
                for (int i = 0; i < stringArr1.length; i++) {           // Create new Task from stringArr1[i]
                    arrTasks = stringArr1[i].toString().split(",");

                    Significance significanceTask = null;
                    Significance[] significances;
                    significances = Significance.values();
                        for (Significance significance : significances) {
                            if (arrTasks[3].equals(significance.mean())) {
                                significanceTask = significance;
                            }
                        }

                    Category categoryTask = null;
                    Category[] categories;
                    categories = Category.values();
                        for (Category category : categories) {
                            if (arrTasks[5].equals(category.mean())) {
                                categoryTask = category;
                            }
                        }

                    Condition conditionTask = null;
                    Condition[] conditions;
                    conditions = Condition.values();
                    for (Condition condition : conditions) {
                        if (arrTasks[6].contains(condition.mean())) {     // Потому что в конце \n
                            conditionTask = condition;
                        }
                    }

                    tmpTask = new Task(arrTasks[1], arrTasks[2], significanceTask, arrTasks[4], categoryTask, conditionTask);
                        for (User user : rezult.getUsers()) {
                            if (Integer.parseInt(arrTasks[0]) == user.hashCode()) {
                                user.getTasks().add(tmpTask);
                            }
                        }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return rezult;
    }

    public void createFilesFromBase(String fileUsers, String fileTasks) {
        // file users
        String line = "";
        try (OutputStream outUser = new FileOutputStream(fileUsers);
             DataOutputStream dataOutUsers = new DataOutputStream(outUser)) {
            for (User user : users) {
                line = user.getFullName() + "," + user.getLogin() + "," + user.getPassword() + "\n";
                dataOutUsers.writeUTF(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // file tasks
        try (OutputStream outTasks = new FileOutputStream(fileTasks);
             DataOutputStream dataOutTasks = new DataOutputStream(outTasks)) {
            for (User user : users) {
                for (Task task : user.getTasks()) {
                    line = user.hashCode() + "," + task.getNumber() + "," + task.getName() + "," + task.getSignificance() +
                            "," + task.getDeadline() + "," + task.getCategory() + "," + task.getCondition() + "\n";
                    dataOutTasks.writeUTF(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void addUser() {
        boolean rezult = false;
        System.out.print("Input fullName >>> ");
        String fullNameUser = scanner.nextLine();
        String[] loginAndPassword = InputLoginAndPassword();
        String loginUser = loginAndPassword[0];
        String passwordUser = loginAndPassword[1];
        User user = new User(fullNameUser, loginUser, passwordUser);
        boolean check = checkUser(loginAndPassword);
        if (!check) {
            rezult = getUsers().add(user);
            if (rezult) {
                System.out.println("Add user is successful");
            } else {
                System.out.println("Add user isn't successful");
            }
        } else {
            System.out.println("User with such login and password is existing");
        }
    }


    public void removeUser(User tmp) {
        boolean rezult;
        rezult = getUsers().remove(tmp);
        if (rezult) {
            System.out.println("Remove user is successful");
        } else {
            System.out.println("Remove user isn't successful");
        }
    }


    public static String[] InputLoginAndPassword() {
        String[] rezult = new String[2];
        System.out.print("Input login >>> ");
        rezult[0] = scanner1.nextLine();
        System.out.print("Input password >>> ");
        rezult[1] = scanner1.nextLine();
        return rezult;
    }


    public boolean checkUser(String[] loginAndPassword) {
        boolean rezult = false;
        String loginUser = loginAndPassword[0];
        String passwordUser = loginAndPassword[1];
        for (User user : users) {
            if (user.getLogin().equals(loginUser) && user.getPassword().equals(passwordUser)) {
                rezult = true;
                break;
            }
        }
        return rezult;
    }

    public User findUser(String[] loginAndPassword) {
        User tmp = null;
        String loginUser = loginAndPassword[0];
        String passwordUser = loginAndPassword[1];
        for (User user : this.getUsers()) {
            if (loginUser.equals(user.getLogin()) && passwordUser.equals(user.getPassword())) {
                tmp = user;
                break;
            }
        }
        return tmp;
    }


}

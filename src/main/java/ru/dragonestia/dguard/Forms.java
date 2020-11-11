package ru.dragonestia.dguard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import ru.dragonestia.dguard.elements.*;
import ru.dragonestia.dguard.exceptions.*;
import ru.nukkitx.forms.elements.CustomForm;
import ru.nukkitx.forms.elements.ImageType;
import ru.nukkitx.forms.elements.SimpleForm;

import java.util.ArrayList;
import java.util.List;

public class Forms {

    private final DGuard main;

    public Forms(DGuard main){
        this.main = main;
    }

    public void f_menu(Player player) {
        new SimpleForm("Меню").addButton("Управление регионами", ImageType.PATH, "textures/items/book_writable")
                .addButton("Создать регион", ImageType.PATH, "textures/items/campfire")
                .addButton("Мои регионы", ImageType.PATH, "textures/items/book_normal")
                .addButton("Информация о регионе", ImageType.PATH, "textures/items/map_empty")
                .addButton("Гайд", ImageType.PATH, "textures/items/book_portfolio")
                .send(player, (targetPlayer, form, data) -> {
                    switch (data) {
                        case 0: //Управление регионами
                            f_control_list(player);
                            break;

                        case 1: //Создать регион
                            f_create_region_menu(player);
                            break;

                        case 2: //Мои регионы
                            f_regions_list(player);
                            break;

                        case 3: //Информация о регионе
                            Region region = new Point(player.getFloorX(), player.getFloorZ(), player.getLevel()).getRegion();

                            if (region == null) {
                                player.sendMessage("§e§lВ данном несте нет региона.");
                                return;
                            }
                            f_region_info(player, region);
                            break;

                        case 4: //Туториал
                            f_guide(player);
                            break;
                    }
                });
    }

    public void f_guide(Player player) {
        new SimpleForm("Гайд")
                .setContent("§l§6Инструкция по созданию региона:§r§f\n" +
                        " Чтобы создать регион нужно сначала выделить крайние точки, которой будут служить границой региона. Отметить точки можно с помощью команд §b/rg pos1§f и §b/rg pos2§f. " +
                        "После выделения точек можно создавать регион в том же разделе меню. \n" +
                        " Регион создаются во всю высоту мира и рассчет блоков идет по площади. Можно создать всего 2 региона, в котором каждый может достигать площади до 10000 блоков или территорию 100 на 100 блоков.\n" +
                        "\n" +
                        "\n" +
                        "§l§6Инструкция по добавлению игрока в регион:§r§f\n" +
                        " Чтобы добавить в регион игрока нужно сначала выбрать регион в разделе §eМои регионы§f, где далее нужно выбрать §eДобавить игрока§f. Выбрав ник игрока из присутствующего онлайна на сервере и нажимаем §eОтправить§f. Готово! Игрок добавлен в регион и теперь имеет роль §bГость§f. Кстати, в регион можно добавить только игрока, который сейчас играет на сервере, иначе он не отобразится в списке игроков.\n" +
                        " Чтобы изменить роль игроку нужно выбрать регион, в которого добавили игрока и выбрать раздел §eУправление игроками§f после чего выбираем нужного нам игрока и изменяем ему роль. В этом же разделе можно как и назначить роль, так и выгнать или передать регион другому игроку.\n" +
                        " Доступные роли для игроков в регионе:\n" +
                        " - §bГость§f - Может только взаимодействовать с печками, сундуками и дверьми. Хорошо подойдет для приюченных игроков.\n" +
                        " - §bЖитель§f - Может строить в привате, также взаимодействовать с сундуками, печками и дверями. Добавлять только на свой страх и риск, ведь администрация не несет ответственности за разрушенный дом.\n" +
                        " - §bВладелец§f - Полностью управляет регионом, выдает роли другим игрокам в регионе.")
                .addButton("Назад")
                .send(player, (targetPlayer, form, data) -> {
                    if (data == -1) return;

                    f_menu(player);
                });
    }

    public void f_region_info(Player player, Region region) {
        if (!region.isExist()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        SimpleForm form = new SimpleForm("Регион " + region.getId());

        String members, guests;

        if (region.getMembers().size() == 0) members = "§eОтсутствуют§f";
        else {
            members = "§3" + String.join(" ", region.getMembers()) + "§f";
        }

        if (region.getGuests().size() == 0) guests = "§eОтсутствуют§f";
        else {
            guests = "§3" + String.join(" ", region.getGuests()) + "§f";
        }

        StringBuilder flags = new StringBuilder();
        for(Flag flag: main.getFlags().values()){
            flags.append(" §2").append(flag.getName()).append("§f - ").append(flag.getValue(region) ? "§aДа" : "§cНет").append("§f\n");
        }

        form.setContent(
                "§lИнформация о регионе §d" + region.getId() + "§f:§r\n" +
                        " §fВладелец региона: §e" + region.getOwner() + "§f\n" +
                        " §fЖители региона: " + members + "§f.\n" +
                        " §fГости: " + guests + "§f.\n" +
                        " §fПлощадь региона: §b" + region.getSize() + "§f(§e" + region.getLength() +"§6x§e" + region.getWeight() + ")\n" +
                        "\n§l§fФлаги:§r\n" + flags
        );

        form.send(player);

    }

    public void f_regions_list(Player player) {
        SimpleForm form = new SimpleForm("Ваши регионы");

        List<Region> regions = new RegionManager(player, main).getRegions();

        if (regions.size() == 0) {
            form.setContent("У вас еще нет регионов.")
                    .addButton("Назад")
                    .send(player, (targetPlayer, targetForm, data) -> {
                        if (data == -1) return;

                        f_menu(player);
                    });

            return;
        }

        form.setContent("Выберите регион, который информацию которого вы хотите посмотреть.");

        for (Region region : regions) {
            form.addButton(region.getId(), ImageType.PATH, "textures/items/campfire");

        }
        form.addButton("Назад")
                .send(player, (targetPlayer, targetForm, data) -> {
                    if (data == -1) return;

                    if (regions.size() == data) {
                        f_menu(player);
                        return;
                    }

                    f_region_info(player, regions.get(data));
                });
    }

    public void f_create_region_menu(Player player) {
        new SimpleForm("Создание региона")
                .setContent(
                        "Установите 2 точки, которые будут выделять территорию для создания региона. Далее можно будет создавать регион.\n" +
                                "\n" +
                                "Просто нажимайте деревянным топором по блоам чтобы устанавливать точки.\n" +
                                "\n" +
                                "Также можно с помощью команд §b/rg pos1§f и §b/rg pos2§f." +
                                "\n" +
                                "Примечание: §3Регион создается во всю высоту, а блоки расчитываются по площади территории.§f"
                )
                .addButton("Создать регион", ImageType.PATH, "textures/items/campfire")
                .addButton("Назад")
                .send(player, (targetPlayer, form, data) -> {
                    if (data == -1) return;

                    switch (data) {
                        case 0:
                            f_create_region(player);
                            break;

                        case 1:
                            f_menu(player);
                            break;
                    }
                });
    }

    public void f_create_region(Player player) {
        new CustomForm("Создание региона")
                .addLabel("Укажите желаемое название региона. Использовать можно только латинские буквы и цифры. Не использовать пробелы!")
                .addInput("Название региона", "Название региона. Например: " + player.getName())
                .send(player, (targetPlayer, form, data) -> {
                    if (data == null) return;

                    String input = data.get(1).toString().trim().replace('.', '-');


                    if (input.length() < 3 || input.length() > 16) {
                        player.sendMessage("§c§lНеверная длина названия региона.");
                        return;
                    }

                    if (!(Point.firstPoints.containsKey(player) && Point.secondPoints.containsKey(player))) {
                        player.sendMessage("§c§lВы не выделили территорию чтобы создать регион.");
                        return;
                    }

                    try {
                        new RegionManager(player, main).createRegion(input, targetPlayer.level.getName(), Point.firstPoints.get(player), Point.secondPoints.get(player));
                        player.sendMessage("§e§lРегион §6" + input + "§e был успешно создан!");
                    } catch (PointsInDifferentLevelsException ex) {
                        player.sendMessage("§c§lТочки территории находятся в разных мирах.");
                    } catch (RegionAlreadyExistException ex) {
                        player.sendMessage("§c§lРегион с таким названием уже существует!");
                    } catch (RegionLimitCountException ex) {
                        player.sendMessage("§c§lВы сейчас владеете максимальным количеством регионов.");
                    } catch (RegionLimitSizeException ex) {
                        player.sendMessage("§c§lВы выделили слишком большую территорию для региона.");
                    } catch (RegionIsCharacterizedByOtherRegionsException ex) {
                        player.sendMessage("§c§lРегион пересекает чужие регионы.");
                    } catch (InvalidRegionIdException e) {
                        player.sendMessage("§c§lВ названии региона присутствуют недопустимые символы.");
                    }
                });
    }

    public void f_control_list(Player player) {
        SimpleForm form = new SimpleForm("Управление регионами");

        List<Region> regions = new RegionManager(player, main).getRegions();

        if (regions.size() == 0) {
            form.setContent("У вас еще нет регионов.")
                    .addButton("Назад")
                    .send(player, (targetPlayer, targetForm, data) -> {
                        if (data == -1) return;

                        f_menu(player);
                    });

            return;
        }

        form.setContent("Выберите регион, который хотите редактировать.");

        for (Region region : regions) {
            form.addButton(region.getId(), ImageType.PATH, "textures/items/campfire");
        }

        form.addButton("Назад")
                .send(player, (targetPlayer, targetForm, data) -> {
                    if (data == -1) return;

                    if (regions.size() == data) {
                        f_menu(player);
                        return;
                    }

                    f_edit_menu(player, regions.get(data));
                });
    }

    public void f_edit_menu(Player player, Region region) {
        if (!region.isExist()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        SimpleForm form = new SimpleForm("Управление регионом " + region.getId());

        form.setContent("Выберите нужное вам действие, которое хотите применить к данному региону.")
                .addButton("Флаги региона", ImageType.PATH, "textures/items/repeater")
                .addButton("Управление игроками", ImageType.PATH, "textures/items/name_tag")
                .addButton("Добавить игрока", ImageType.PATH, "textures/items/cake")
                .addButton("Удалить регион", ImageType.PATH, "textures/items/blaze_powder")
                .addButton("Назад");

        form.send(player, (targetPlayer, targetForm, data) -> {
            if (data == -1) return;

            switch (data) {
                case 0: //Флаги региона
                    f_edit_flag(player, region);
                    break;

                case 1: //Управление игроками
                    f_edit_players(player, region);
                    break;

                case 2: //Добавить игрока
                    f_add_user(player, region);
                    break;

                case 3: //Удалить регион
                    f_delete(player, region);
                    break;

                case 4:
                    f_control_list(player);
                    break;
            }

        });
    }

    public void f_edit_flag(Player player, Region region) {
        if (!region.isExist()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        CustomForm form = new CustomForm("Управление флагами")
                .addLabel("Установите нужные параметры установки флагов для региона §b" + region.getId() + "§f.");

        for (Flag flag : main.getFlags().values()) {
            form.addToggle(flag.getName(), region.getFlag(flag));
        }

        form.send(player, (targetPlayer, targetForm, data) -> {
            if (data == null) return;

            data.remove(0);

            List<Flag> flags = new ArrayList<>(main.getFlags().values());

            for (int i = 0; i < data.size(); i++) {
                region.setFlag(flags.get(i), (boolean) data.get(i));
            }

            player.sendMessage("§e§lФлаги были успешно изменены!");
        });
    }

    public void f_delete(Player player, Region region) {
        if (!region.isExist()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        SimpleForm form = new SimpleForm("Удаление региона " + region.getId(), "Подтвердите что вы точно хотите удалить регион §b" + region.getId() + "§f");

        form.addButton("Удалить регион", ImageType.PATH, "textures/blocks/barrier")
                .addButton("Назад");

        form.send(player, (targetPlayer, targetForm, data) -> {
            if (data == -1) return;

            if (data == 0) {
                player.sendMessage("§e§lРегион §6" + region.getId() + "§e был успешно удален!");
                region.remove();
            } else f_edit_menu(player, region);
        });

    }

    public void f_add_user(Player player, Region region) {
        if (!region.isExist()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        CustomForm form = new CustomForm("Добавить игрока");

        List<String> players = new ArrayList<>();

        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            if (!region.getRole(p.getName()).equals(Role.Nobody)) players.remove(player.getName());
            else players.add(p.getName());
        }

        if (players.size() == 0) {
            form.addLabel("На сервере сейчас нет игроков, которые не состоят в регионе.");
            form.send(player);
            return;
        }

        form.addLabel("Выберите игрока, которого хотите добавить в ваш регион. После добавления игроку устанавливается роль §bГость§f.")
                .addDropDown("Список игроков", players);

        form.send(player, (targetPlayer, targetForm, data) -> {
            if (data == null) return;

            String p = data.get(1).toString();

            region.setRole(p, Role.Guest);

            player.sendMessage("§e§lИгрок §6" + p + "§e был успешно добавлен в регион §6" + region.getId() + "§e!");
            if (Server.getInstance().getPlayer(p) != null)
                Server.getInstance().getPlayer(p).sendMessage("§e§lИгрок §6" + player.getName() + "§e добавил вас в регион §6" + region.getId() + "§e.");
        });
    }

    public void f_edit_players(Player player, Region region) {
        if (!region.isExist()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        SimpleForm form = new SimpleForm("Управление игроками");

        form.setContent(
                "Выберите нужного игрока для редактирования роли в регионе §e" + region.getId() + "§f.\n" +
                        "\n" +
                        "Типы игроков:\n" +
                        "§bЗеленые§f - Жители региона.\n" +
                        "§bБирюзовые§f - Гости региона."
        );

        for (String member : region.getMembers()) {
            form.addButton(member, ImageType.PATH, "textures/blocks/concrete_lime");
        }

        for (String guest : region.getGuests()) {
            form.addButton(guest, ImageType.PATH, "textures/blocks/concrete_light_blue");
        }

        form.addButton("Назад");

        form.send(player, (targetPlayer, targetForm, data) -> {
            if (data == -1) return;

            List<String> users = new ArrayList<>(region.getMembers());
            users.addAll(region.getGuests());

            if (users.size() == data) {
                f_edit_menu(player, region);
                return;
            }

            f_edit_role(player, users.get(data), region);
        });

    }

    public void f_edit_role(Player player, String p, Region region) {
        if (!region.isExist()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        CustomForm form = new CustomForm("Изменение роли");

        List<String> actions = new ArrayList<>();
        actions.add("Выгнать из региона");
        actions.add("Назначить роль 'Гость'");
        actions.add("Назначить роль 'Житель'");
        actions.add("Передать регион");

        form.addLabel("Выберите действие для игрока §b" + p + "§f в регионе §b" + region.getId() + "§f.")
                .addDropDown("Действие", actions);

        form.send(player, (targetPlayer, targetForm, data) -> {
            if (data == null) return;

            String action = data.get(1).toString();
            Role role;

            switch (actions.indexOf(action)) {
                case 3:
                    role = Role.Owner;

                    player.sendMessage("§e§lВы успешно передали регион §6" + region.getId() + "§e игроку §6" + p + "§e.");
                    if (Server.getInstance().getPlayer(p) != null)
                        Server.getInstance().getPlayer(p).sendMessage("§e§lИгрок §6" + player.getName() + "§6 передал вам регион §6" + region.getId() + "§e.");
                    break;

                case 2:
                    role = Role.Member;

                    player.sendMessage("§e§lВы успешно выдали роль§6 Житель§e игроку §6" + p + "§e в регионе §6" + region.getId() + "§e.");
                    if (Server.getInstance().getPlayer(p) != null)
                        Server.getInstance().getPlayer(p).sendMessage("§e§lИгрок §6" + player.getName() + "§6 установил вам роль§6 Житель§e в регионе §6" + region.getId() + "§e.");
                    break;

                case 1:
                    role = Role.Guest;

                    player.sendMessage("§e§lВы успешно выдали роль§6 Гость§e игроку §6" + p + "§e в регионе §6" + region.getId() + "§e.");
                    if (Server.getInstance().getPlayer(p) != null)
                        Server.getInstance().getPlayer(p).sendMessage("§e§lИгрок §6" + player.getName() + "§6 установил вам роль§6 Гость§e в регионе §§6" + region.getId() + "§e.");
                    break;

                default:
                    role = Role.Nobody;

                    player.sendMessage("§e§lВы успешно выгнали игрока §6" + p + "§e из региона §6" + region.getId() + "§e.");
                    if (Server.getInstance().getPlayer(p) != null)
                        Server.getInstance().getPlayer(p).sendMessage("§e§lИгрок §6" + player.getName() + "§6 выгнал вас из региона §6" + region.getId() + "§e.");
            }

            region.setRole(p, role);
        });
    }

}

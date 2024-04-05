package ru.dragonestia.dguard;

import cn.nukkit.Player;
import cn.nukkit.Server;
import ru.contentforge.formconstructor.form.CustomForm;
import ru.contentforge.formconstructor.form.ModalForm;
import ru.contentforge.formconstructor.form.SimpleForm;
import ru.contentforge.formconstructor.form.element.*;
import ru.dragonestia.dguard.exceptions.RegionException;
import ru.dragonestia.dguard.region.Flag;
import ru.dragonestia.dguard.region.PlayerRegionManager;
import ru.dragonestia.dguard.region.Region;
import ru.dragonestia.dguard.region.Role;
import ru.dragonestia.dguard.util.Area;
import ru.dragonestia.dguard.util.Point;

import java.util.ArrayList;
import java.util.List;

public class Forms {

    private final DGuard main;

    public Forms(DGuard main){
        this.main = main;
    }

    public void sendMainForm(Player player) {
        Region region = new Point(player).getRegion(player.getLevel());

        SimpleForm form = new SimpleForm("Меню")
                .add(new Button("Управление регионами", Button.Icon.texture("textures/items/book_writable"), (p, b) -> sendOwnRegionsForm(player)))
                .add(new Button("Создать регион", Button.Icon.texture("textures/items/campfire"), (p, b) -> sendPreCreateForm(player)))
                .add(new Button("Мои регионы", Button.Icon.texture("textures/items/book_normal"), (p, b) -> sendRegionsListForm(player)));

        if(region != null){
             form.add(new Button("Информация о текущем регионе", Button.Icon.texture("textures/items/map_empty"), (p, b) -> sendRegionInfoForm(player, region)));

             if(region.getOwner().equalsIgnoreCase(player.getName())){
                 form.add(new Button("Управление текущим регионом", Button.Icon.texture("textures/gui/newgui/Realms"), (p, b) -> sendEditRegionMenuForm(p, region)));
             }
        }

        form.add(new Button("Гайд", Button.Icon.texture("textures/items/book_portfolio"), (p, b) -> sendGuideForm(player)))
                .send(player);
    }

    public void sendGuideForm(Player player) {
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
                .add(new Button("Назад", (p, b) -> sendMainForm(player)))
                .send(player);
    }

    public void sendRegionInfoForm(Player player, Region region) {
        if (region.isClosed()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        SimpleForm form = new SimpleForm("Регион §l'" + region.getName() + "'");

        String members, guests;

        if (region.getMembers().isEmpty()) members = "§eОтсутствуют§f";
        else {
            members = "§3" + String.join(" ", region.getMembers()) + "§f";
        }

        if (region.getGuests().isEmpty()) guests = "§eОтсутствуют§f";
        else {
            guests = "§3" + String.join(" ", region.getGuests()) + "§f";
        }

        StringBuilder flags = new StringBuilder();
        for(Flag flag: main.getFlags().values()){
            flags.append(" ").append(flag.getValue(region) ? "\uE0A0" : "\uE0A1").append(" ").append(flag.getName()).append("§f").append("§f\n\n");
        }

        Area area = region.getArea();
        form.setContent(
                "§lИнформация о регионе §d" + region.getName() + "§f(id: §7" + region.getId() + "§f):§r\n" +
                        " §fВладелец региона: §e" + region.getOwner() + "§f\n" +
                        " §fЖители региона: " + members + "§f.\n" +
                        " §fГости: " + guests + "§f.\n" +
                        " §fПлощадь региона: §b" + area.getSpace(main.getSettings().is_3d()) + "§f(§e" + area.deltaX() + (main.getSettings().is_3d()? ("§6x§e" + area.deltaY()) : "") + "§6x§e" + area.deltaZ() + ")\n" +
                        "\n§l§fФлаги:§r\n\n" + flags
        );

        if(region.getRole(player.getName()).equals(Role.Owner)){
            form.add(new Button("Управление регионом", Button.Icon.texture("textures/ui/gear"), (p, b) -> sendEditRegionMenuForm(p, region)));
        }

        form.send(player);

    }

    public void sendRegionsListForm(Player player) {
        SimpleForm form = new SimpleForm("Ваши регионы");

        List<Region> regions = new PlayerRegionManager(player, main).getRegions();

        if (regions.size() == 0) {
            form.setContent("У вас еще нет регионов.")
                    .add(new Button("Назад", (p, b) -> sendMainForm(player)))
                    .send(player);

            return;
        }

        form.setContent("Выберите регион, который информацию которого вы хотите посмотреть.");

        for (Region region: regions) {
            form.add(new Button("§l" + region.getName() + "\n§r§8(id: "+region.getId()+")", Button.Icon.texture("textures/items/campfire"), (p, b) -> sendRegionInfoForm(player, region)));

        }
        form.add(new Button("Назад", (p, b) -> sendMainForm(player)))
                .send(player);
    }

    public void sendPreCreateForm(Player player) {
        new SimpleForm("Создание региона")
                .setContent(
                        "Установите 2 точки, которые будут выделять территорию для создания региона. Далее можно будет создавать регион.\n" +
                                "\n" +
                                "Просто выделяйте крайние точки с помощью команд §b/rg pos1§f и §b/rg pos2§f.\n" +
                                (main.getSettings().is_3d()? "" : "\nПримечание: §3Регион создается во всю высоту, а блоки расчитываются по площади территории.§f")
                )
                .add(new Button("Создать регион", Button.Icon.texture("textures/items/campfire"), (p, b) -> sendCreateRegionForm(player)))
                .add(new Button("Назад", (p, b) -> sendMainForm(player)))
                .send(player);
    }

    public void sendCreateRegionForm(Player player) {
        new CustomForm("Создание региона")
                .add("Укажите желаемое название региона. Допустимы абсолютно любые символы.")
                .add("rg-name", new Input("Название региона", "Название региона. Например: " + player.getName()))
                .setHandler((p, response) -> {
                    String regionName = response.getInput("rg-name").getValue().trim().replace('.', '-');


                    if (regionName.length() < 3 || regionName.length() > 16) {
                        player.sendMessage("§c§lНеверная длина названия региона.");
                        return;
                    }

                    if (!(main.getFirstPoints().containsKey(p.getId()) && main.getSecondPoints().containsKey(p.getId()))) {
                        player.sendMessage("§c§lВы не выделили территорию чтобы создать регион.");
                        return;
                    }

                    Area area = new Area(main.getFirstPoints().get(p.getId()), main.getSecondPoints().get(p.getId()));

                    try {
                        new PlayerRegionManager(player, main).createRegion(regionName, area, p.getLevel());
                        player.sendMessage("§e§lРегион §6" + regionName + "§e был успешно создан!");
                    } catch (RegionException ex) {
                        player.sendMessage("§c§l"+ex.getMessage()+".");
                    }
                }).send(player);
    }

    public void sendOwnRegionsForm(Player player) {
        SimpleForm form = new SimpleForm("Управление регионами");

        List<Region> regions = new PlayerRegionManager(player, main).getRegions();

        if (regions.isEmpty()) {
            form.setContent("У вас еще нет регионов.")
                    .add(new Button("Назад", (p, b) -> sendMainForm(player)))
                    .send(player);
            return;
        }

        form.setContent("Выберите регион, который хотите редактировать.");

        for (Region region : regions) {
            form.add(new Button("§r" + region.getName() + "\n§r§8(id: "+region.getId()+")", Button.Icon.texture("textures/items/campfire"), (p, b) -> sendEditRegionMenuForm(player, region)));
        }

        form.add(new Button("Назад", (p, b) -> sendMainForm(player)))
                .send(player);
    }

    public void sendEditRegionMenuForm(Player player, Region region) {
        if (region.isClosed()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        SimpleForm form = new SimpleForm("Управление регионом §l'" + region.getName() + "'");

        form.setContent("Выберите нужное вам действие, которое хотите применить к данному региону.")
                .add(new Button("Флаги региона", Button.Icon.texture("textures/items/repeater"), (p, b) -> sendEditionFlagsForm(player, region)))
                .add(new Button("Управление игроками", Button.Icon.texture("textures/items/name_tag"), (p, b) -> sendEditPlayersForm(player, region)))
                .add(new Button("Добавить игрока", Button.Icon.texture("textures/items/cake"), (p, b) -> sendAddUserForm(player, region)))
                .add(new Button("Удалить регион", Button.Icon.texture("textures/items/blaze_powder"), (p, b) -> sendDeleteRegionForm(player, region)))
                .add(new Button("Назад", (p, b) -> sendOwnRegionsForm(player)));

        form.send(player);
    }

    public void sendEditionFlagsForm(Player player, Region region) {
        if (region.isClosed()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        CustomForm form = new CustomForm("Управление флагами")
                .add("Установите нужные параметры установки флагов для региона §b" + region.getName() + "§f.");

        for (Flag flag : main.getFlags().values()) {
            form.add(flag.getDescription());
            form.add(flag.getId(), new Toggle(flag.getName(), region.getFlag(flag)));
        }

        form.setHandler((p, response) -> {
            for(Flag flag: main.getFlags().values()){
                region.setFlag(flag, response.getToggle(flag.getId()).getValue());
            }

            player.sendMessage("§e§lФлаги были успешно изменены!");
            region.save(true);
        }).send(player);
    }

    public void sendDeleteRegionForm(Player player, Region region) {
        if (region.isClosed()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        new ModalForm("Удаление региона")
                .setContent("Подтвердите что вы точно хотите удалить регион §b" + region.getName() + "§f(id: §7" + region.getId() + "§f)")
                .setPositiveButton("§l§4Удалить регион")
                .setNegativeButton("Назад")
                .setHandler((p, data) -> {
                    if (data) {
                        player.sendMessage("§e§lРегион §6" + region.getName() + "§e был успешно удален!");
                        region.remove();
                    } else sendEditRegionMenuForm(player, region);
                }).send(player);
    }

    public void sendAddUserForm(Player player, Region region) {
        if (region.isClosed()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        CustomForm form = new CustomForm("Добавить игрока")
                .add("Добавлять можно только игроков, которые стоят рядом с вами.");

        List<SelectableElement> players = new ArrayList<>();
        for (Player p: player.getLevel().getPlayers().values()) {
            if (p.distanceSquared(player) > 50 * 50 || !region.getRole(p.getName()).equals(Role.Nobody)) continue;
            players.add(new SelectableElement(p.getName(), p));
        }

        if (players.size() == 0) {
            form.add("§с§lРядом с вами нет игроков, которых можно добавить в регион.").send(player);
            return;
        }

        form.add("Выберите игрока, которого хотите добавить в ваш регион. После добавления игроку устанавливается роль §bГость§f.")
                .add("target", new Dropdown("Список игроков", players))
                .setHandler((p, response) -> {
                    Player target = response.getDropdown("target").getValue().getValue(Player.class);

                    region.setRole(target, Role.Guest);

                    p.sendMessage("§e§lИгрок §6" + target.getName() + "§e был успешно добавлен в регион §6" + region.getName() + "§e!");
                    if (target.isOnline())
                        target.sendMessage("§e§lИгрок §6" + p.getName() + "§e добавил вас в регион §6" + region.getName() + "§e.");
                    region.save(true);
                }).send(player);
    }

    public void sendEditPlayersForm(Player player, Region region) {
        if (region.isClosed()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        SimpleForm form = new SimpleForm("Управление игроками");

        form.setContent(
                "Выберите нужного игрока для редактирования роли в регионе §e" + region.getName() + "§f.\n" +
                        "\n" +
                        "Типы игроков:\n" +
                        "§bЗеленые§f - Жители региона.\n" +
                        "§bБирюзовые§f - Гости региона."
        );

        for (String member : region.getMembers()) {
            form.add(new Button(member, Button.Icon.texture("textures/blocks/concrete_lime"), (p, b) -> sendEditRoleForm(p, member, region)));
        }

        for (String guest : region.getGuests()) {
            form.add(new Button(guest, Button.Icon.texture("textures/blocks/concrete_light_blue"), (p, b) -> sendEditRoleForm(p, guest, region)));
        }

        form.add(new Button("Назад", (p, b) -> sendEditRegionMenuForm(player, region)))
                .send(player);

    }

    public void sendEditRoleForm(Player player, String target, Region region) {
        if (region.isClosed()) {
            player.sendMessage("§cРегион не найден!");
            return;
        }

        CustomForm form = new CustomForm("Изменение роли");

        List<SelectableElement> actions = new ArrayList<>();
        actions.add(new SelectableElement("Выгнать из региона", Role.Nobody));
        actions.add(new SelectableElement("Назначить роль 'Гость'", Role.Guest));
        actions.add(new SelectableElement("Назначить роль 'Житель'", Role.Member));
        actions.add(new SelectableElement("Передать регион", Role.Owner));

        form.add("Выберите действие для игрока §b" + target + "§f в регионе §b" + region.getName() + "§f.")
                .add("action", new Dropdown("Действие", actions))
                .setHandler((p, response) -> {
                    Role role = response.getDropdown("action").getValue().getValue(Role.class);
                    switch (role) {
                        case Owner:
                            player.sendMessage("§e§lВы успешно передали регион §6" + region.getName() + "§e игроку §6" + target + "§e.");
                            if (Server.getInstance().getPlayer(target) != null)
                                Server.getInstance().getPlayer(target).sendMessage("§e§lИгрок §6" + player.getName() + "§6 передал вам регион §6" + region.getName() + "§e.");
                            break;

                        case Guest:
                        case Member:
                            player.sendMessage("§e§lВы успешно выдали роль§6 "+role.getName()+"§e игроку §6" + target + "§e в регионе §6" + region.getName() + "§e.");
                            if (Server.getInstance().getPlayer(target) != null)
                                Server.getInstance().getPlayer(target).sendMessage("§e§lИгрок §6" + player.getName() + "§6 установил вам роль§6 "+role.getName()+"§e в регионе §6" + region.getName() + "§e.");
                            break;

                        default:
                            player.sendMessage("§e§lВы успешно выгнали игрока §6" + target + "§e из региона §6" + region.getName() + "§e.");
                            if (Server.getInstance().getPlayer(target) != null)
                                Server.getInstance().getPlayer(target).sendMessage("§e§lИгрок §6" + player.getName() + "§6 выгнал вас из региона §6" + region.getName() + "§e.");
                    }

                    region.setRole(target, role);
                    region.save(true);
                }).send(player);
    }

}
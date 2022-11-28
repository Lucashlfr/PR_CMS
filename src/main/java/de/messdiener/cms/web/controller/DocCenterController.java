package de.messdiener.cms.web.controller;

import de.messdiener.cms.app.entities.file.FileEntity;
import de.messdiener.cms.app.entities.protocol.Protocol;
import de.messdiener.cms.app.entities.protocol.cache.ProtocolComment;
import de.messdiener.cms.app.entities.protocol.cache.ProtocolElement;
import de.messdiener.cms.cache.Cache;
import de.messdiener.cms.web.security.SecurityHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Controller
public class DocCenterController {

    @GetMapping("/dc/cloud")
    public String cloud(HttpSession httpSession, Model model) throws SQLException {

        SecurityHelper.addSessionUser(httpSession);

        model.addAttribute("yor_files", Cache.CLOUD_SERVICE.getYourFiles());
        model.addAttribute("files", Cache.CLOUD_SERVICE.getFiles());

        return "documents/cloud";
    }

    @PostMapping(path = "/upload", consumes = {"multipart/form-data"})
    public RedirectView uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() < 1048576) {
                Cache.CLOUD_SERVICE.save(FileEntity.convert(file, SecurityHelper.getUser().getUser_UUID().toString()));
            }
            return new RedirectView("/dc/cloud", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView("/error");
        }
    }

    @GetMapping("/public/dc/cloud/file")
    public ResponseEntity<?> dowloadPublic(@RequestParam("uuid") String uuid) throws SQLException {
        return getResponseEntity(uuid);
    }

    @GetMapping("/dc/cloud/file")
    public ResponseEntity<?> dowload(@RequestParam("uuid") String uuid) throws SQLException {
        return getResponseEntity(uuid);
    }

    @GetMapping("/download")
    public ResponseEntity<?> dowloadStatic(@RequestParam("uuid") String uuid) throws SQLException {
        return getResponseEntity(uuid);
    }

    private static ResponseEntity<byte[]> getResponseEntity(String uuid) throws SQLException {
        FileEntity file = Cache.CLOUD_SERVICE.get(uuid);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(file.getMediaType())
                .body(file.getData());
    }

    @GetMapping("/dc/cloud/file/delete")
    public RedirectView delete(@RequestParam("uuid") String uuid) throws SQLException {
        FileEntity file = Cache.CLOUD_SERVICE.get(uuid);

        Cache.CLOUD_SERVICE.delete(file.getUUID());

        return new RedirectView("/dc/cloud");
    }

    @GetMapping("/dc/protocol")
    public String indexPK(HttpSession httpSession, Model model, @RequestParam("uuid") Optional<String> inputUUID) throws SQLException {

        SecurityHelper.addSessionUser(httpSession);

        if (inputUUID.isPresent()) {
            Protocol protocol = Cache.CLOUD_SERVICE.getProtocols()
                    .stream()
                    .filter(p -> p.getUUID().equals(UUID.fromString(inputUUID.get())))
                    .findFirst().orElseThrow();

            model.addAttribute("protocol", protocol);
            model.addAttribute("currentUser", SecurityHelper.getUser());

            return "documents/protocols/protocolInterface";
        }
        model.addAttribute("protocols", Cache.CLOUD_SERVICE.getProtocols());
        return "documents/protocols/protocols";
    }

    @PostMapping("/dc/protocol/create")
    public RedirectView createProtocol(@RequestParam("name") String name) throws SQLException {

        Protocol protocol = Protocol.empty();
        protocol.setRequest_name(name);

        Cache.CLOUD_SERVICE.saveProtocol(protocol);

        return new RedirectView("/dc/protocol");

    }

    @PostMapping("/dc/protocol/comment/create")
    public RedirectView createProtocol(@RequestParam("text") String text, @RequestParam("uuid")UUID uuid) throws SQLException {

        ProtocolComment protocolComment = new ProtocolComment(UUID.randomUUID(), uuid, SecurityHelper.getUser().getUser_UUID(), System.currentTimeMillis(),text);
        protocolComment.save();

        return new RedirectView("/dc/protocol?uuid=" + uuid);

    }

    @PostMapping("/dc/protocol/element/create")
    public RedirectView createProtocol(@RequestParam("uuid") String uuid, @RequestParam("headline")String headline,
                                       @RequestParam("background")String background,@RequestParam("icon")String icon,
                                       @RequestParam("text")String text) throws SQLException {

        ProtocolElement protocolElement = new ProtocolElement(UUID.randomUUID(), UUID.fromString(uuid), background, icon, headline, text);
        protocolElement.save();

        return new RedirectView("/dc/protocol?uuid=" + uuid);
    }

    @PostMapping("/dc/protocol/description/save")
    public RedirectView saveDescription(@RequestParam("uuid")String uuid, @RequestParam("title")String title, @RequestParam("description")String text) throws SQLException {
        Protocol protocol = Cache.CLOUD_SERVICE.getProtocols().stream().filter(pr -> pr.getUUID().toString().equals(uuid)).findFirst().orElseThrow();

        protocol.setRequest_name(title);
        protocol.setDescription(text);
        protocol.save();

        return new RedirectView("/dc/protocol?uuid=" + uuid);
    }

    @GetMapping("/dc/protocol/delete")
    public RedirectView delete(@RequestParam("uuid")UUID uuid) throws SQLException {
        Protocol protocol = Cache.CLOUD_SERVICE.getProtocols().stream().filter(pr -> pr.getUUID().equals(uuid)).findFirst().orElseThrow();
        Cache.CLOUD_SERVICE.deleteProtocol(protocol);

        return new RedirectView("/dc/protocol");
    }

    @GetMapping("/dc/protocol/remove")
    public RedirectView deleteElement(@RequestParam("element")UUID element, @RequestParam("uuid")UUID protocol) throws SQLException {
        Cache.CLOUD_SERVICE.deleteElement(protocol, element);

        return new RedirectView("/dc/protocol?uuid=" + protocol);
    }

}

package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.request.AiChatRequest;
import com.major.CampuSphere.dto.request.AskDoubtRequest;
import com.major.CampuSphere.dto.response.AiResponse;
import com.major.CampuSphere.dto.response.ConversationResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.entity.AiConversation;
import com.major.CampuSphere.entity.AiMessage;
import com.major.CampuSphere.entity.LearningMaterial;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.exception.ForbiddenException;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.AiConversationRepository;
import com.major.CampuSphere.repository.LearningMaterialRepository;
import com.major.CampuSphere.repository.UserRepository;
import com.major.CampuSphere.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Demo AI implementation.
 *
 * Returns contextual canned responses based on keywords in the question.
 * Persists conversations and messages to the database.
 * Does NOT call any external LLM API.
 *
 * To replace with a real LLM: create RealAiServiceImpl implements AiService,
 * annotate it @Primary, and remove @Primary from this class.
 * The controller and API contract remain unchanged.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class DemoAiServiceImpl implements AiService {

    private final AiConversationRepository conversationRepo;
    private final LearningMaterialRepository materialRepo;
    private final UserRepository userRepo;

    @Value("${app.ai.demo-mode:true}")
    private boolean demoMode;

    // ─────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AiResponse askDoubt(AskDoubtRequest request, Long userId) {
        User user = getUser(userId);

        AiConversation conversation = getOrCreateConversation(
                request.getConversationId(), userId, "DOUBT",
                request.getDocumentTitle(), request.getDocumentId(), user
        );

        // Persist user message
        addMessage(conversation, "USER", request.getQuestion());

        // Generate demo answer
        String answer = generateDoubtAnswer(request);

        // Persist assistant message
        addMessage(conversation, "ASSISTANT", answer);

        log.info("AI doubt handled for user={} conversation={}", userId, conversation.getConversationKey());

        return AiResponse.builder()
                .conversationId(conversation.getConversationKey())
                .answer(answer)
                .reply(answer)
                .sources(List.of())
                .demo(true)
                .build();
    }

    @Override
    @Transactional
    public AiResponse chat(AiChatRequest request, Long userId) {
        User user = getUser(userId);

        AiConversation conversation = getOrCreateConversation(
                request.getConversationId(), userId, "CHAT",
                "Campus AI Chat", null, user
        );

        addMessage(conversation, "USER", request.getMessage());

        String reply = generateChatReply(request.getMessage());

        addMessage(conversation, "ASSISTANT", reply);

        log.info("AI chat handled for user={} conversation={}", userId, conversation.getConversationKey());

        return AiResponse.builder()
                .conversationId(conversation.getConversationKey())
                .answer(reply)
                .reply(reply)
                .sources(List.of())
                .demo(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ConversationResponse> listConversations(Long userId, Pageable pageable) {
        Page<AiConversation> page = conversationRepo.findByUserIdOrderByUpdatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(this::toConversationResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversation(String conversationId, Long userId) {
        AiConversation conv = conversationRepo.findByConversationKey(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId));
        if (!conv.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have access to this conversation");
        }
        return toConversationResponse(conv);
    }

    // ─────────────────────────────────────────────────────────────
    // Demo response generation
    // ─────────────────────────────────────────────────────────────

    private String generateDoubtAnswer(AskDoubtRequest req) {
        String q = req.getQuestion().toLowerCase();
        String ctx = req.getDocumentTitle() != null ? req.getDocumentTitle() : "this material";

        if (q.contains("define") || q.contains("what is") || q.contains("explain")) {
            return String.format(
                "Great question! Based on **%s**, here is a conceptual overview:\n\n" +
                "This concept is fundamental to the subject. It refers to a structured approach " +
                "that enables systematic problem-solving. Key aspects include:\n\n" +
                "1. **Core principle** — The foundational idea that drives this concept\n" +
                "2. **Application** — How it is applied in practice\n" +
                "3. **Significance** — Why it matters in the broader context\n\n" +
                "> 💡 *This is a demo response. Connect a real LLM for precise answers based on the document content.*",
                ctx);
        }
        if (q.contains("difference") || q.contains("compare") || q.contains("vs")) {
            return "Here is a comparison of the two concepts mentioned:\n\n" +
                "| Aspect | Concept A | Concept B |\n" +
                "|--------|-----------|----------|\n" +
                "| Definition | First approach | Second approach |\n" +
                "| Use case | Scenario A | Scenario B |\n" +
                "| Complexity | Lower | Higher |\n\n" +
                "> 💡 *Demo mode — real document analysis coming with LLM integration.*";
        }
        if (q.contains("example") || q.contains("how to") || q.contains("implement")) {
            return String.format(
                "Here is a practical example related to **%s**:\n\n" +
                "```\n// Example pseudocode\nfunction solve(input) {\n  // Step 1: Analyse the problem\n" +
                "  // Step 2: Apply the concept\n  // Step 3: Return result\n  return result;\n}\n```\n\n" +
                "This pattern is commonly used in academic and industry contexts. " +
                "Refer to the relevant section of your notes for the exact implementation.\n\n" +
                "> 💡 *Demo mode — LLM will provide context-aware code examples once integrated.*",
                ctx);
        }
        if (q.contains("formula") || q.contains("equation") || q.contains("calculate")) {
            return "The relevant formula/equation for this topic involves:\n\n" +
                "**General form:** `Result = f(input variables)`\n\n" +
                "Where each variable represents a specific measured or calculated quantity. " +
                "Refer to your lecture notes for the exact symbolic representation.\n\n" +
                "> 💡 *Demo mode — exact formulae will be extracted from the PDF once LLM is integrated.*";
        }
        if (q.contains("summary") || q.contains("summarise") || q.contains("summarize")) {
            return String.format(
                "**Summary of %s:**\n\n" +
                "This material covers the foundational principles and applications of the subject. " +
                "Key takeaways include core theoretical frameworks, practical implementations, " +
                "and real-world use cases.\n\n" +
                "**Main sections:**\n- Introduction and motivation\n- Core concepts\n" +
                "- Worked examples\n- Practice problems\n\n" +
                "> 💡 *Demo mode — accurate AI summaries require LLM integration.*",
                ctx);
        }

        // Default
        return String.format(
            "Thank you for your question about **\"%s\"**.\n\n" +
            "Based on the context of *%s*, this is a relevant area of study. " +
            "I recommend reviewing the relevant section in your notes and cross-referencing " +
            "with your textbook for a complete understanding.\n\n" +
            "If you have a more specific question, feel free to ask — " +
            "I am here to help guide your understanding.\n\n" +
            "> 💡 *This is a demo AI response. Real LLM integration will provide precise, " +
            "document-aware answers.*",
            req.getQuestion(), ctx);
    }

    private String generateChatReply(String message) {
        String m = message.toLowerCase();

        if (m.contains("where") || m.contains("location") || m.contains("block") || m.contains("library")) {
            return "🗺️ **Campus Navigation:**\n\nHere are some key locations on campus:\n\n" +
                "- **Block A** — Main academic building, floors 1–4\n" +
                "- **Block C** — Computer labs and project rooms\n" +
                "- **Central Library** — Open 8AM–10PM\n" +
                "- **Food Court** — Near the central plaza, open 7AM–10PM\n" +
                "- **Incubation Centre** — Block D, for startup projects\n\n" +
                "Use the **Campus Navigation** page for the interactive map!";
        }
        if (m.contains("event") || m.contains("hackathon") || m.contains("fest")) {
            return "🎉 **Upcoming Events:**\n\nThere are several exciting events coming up:\n\n" +
                "- **HackSpire 2026** — Aug 7, Main Auditorium\n" +
                "- **AI Summit 2026** — Sep 1, Seminar Hall\n" +
                "- **Startup Pitch Day** — Sep 15, Incubation Centre\n\n" +
                "Visit the **Events** page to register and get full details!";
        }
        if (m.contains("club") || m.contains("join") || m.contains("society")) {
            return "🏛️ **Campus Clubs:**\n\nWe have several active clubs:\n\n" +
                "- 💻 **Coding Club** — 142 members, currently recruiting\n" +
                "- 🤖 **Robotics Society** — 87 members, currently recruiting\n" +
                "- 🎭 **Cultural Council** — 210 members\n" +
                "- 🚀 **E-Cell** — 95 members, currently recruiting\n\n" +
                "Visit the **Clubs** page to explore and join!";
        }
        if (m.contains("assignment") || m.contains("deadline") || m.contains("due")) {
            return "📅 **Deadlines & Assignments:**\n\nCheck your **Academics** page for your upcoming deadlines. " +
                "Currently tracked deadlines include assignments, quizzes, and project submissions.\n\n" +
                "Make sure to plan your schedule accordingly!";
        }
        if (m.contains("cgpa") || m.contains("grade") || m.contains("attendance") || m.contains("marks")) {
            return "📊 **Academic Performance:**\n\nYour academic details — CGPA, attendance percentage, " +
                "and credit progress — are available on the **My Academics** page.\n\n" +
                "If you have concerns about your performance, reach out to your faculty advisor!";
        }
        if (m.contains("mentor") || m.contains("guidance") || m.contains("help me")) {
            return "🤝 **Mentorship:**\n\nLooking for guidance? Visit the **Mentorship** page to find senior " +
                "students who can help with academics, projects, or career advice.\n\n" +
                "You can filter mentors by skill, branch, and availability!";
        }
        if (m.contains("learn") || m.contains("study") || m.contains("material") || m.contains("notes")) {
            return "📚 **Learning Hub:**\n\nThe Learning Hub has study materials for all semesters:\n\n" +
                "- Notes, PDFs, Videos, Lab Manuals, Handwritten notes\n" +
                "- Organised by semester and subject\n" +
                "- Track your reading progress\n" +
                "- Bookmark materials for quick access\n\n" +
                "You can also open any material and use the **AI Doubt Panel** for subject-specific help!";
        }
        if (m.contains("hello") || m.contains("hi") || m.contains("hey")) {
            return "👋 **Hello! I am the CampuSphere AI Assistant.**\n\n" +
                "I can help you with:\n" +
                "- 🗺️ Campus navigation\n" +
                "- 📅 Events and activities\n" +
                "- 📚 Learning resources\n" +
                "- 🤝 Mentorship guidance\n" +
                "- 📊 Academic information\n\n" +
                "What would you like to know today?";
        }

        return "I understand you are asking about: **\"" + message + "\"**\n\n" +
            "As your campus AI assistant, I can help with navigation, events, clubs, academics, " +
            "and learning resources. Could you be more specific about what you need?\n\n" +
            "> 💡 *This is a demo AI response. Real LLM integration will provide intelligent, " +
            "context-aware answers to any campus-related question.*";
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private AiConversation getOrCreateConversation(String conversationId, Long userId,
                                                    String type, String title,
                                                    String documentId, User user) {
        if (conversationId != null) {
            return conversationRepo.findByConversationKeyAndUserId(conversationId, userId)
                    .orElseGet(() -> createConversation(userId, type, title, documentId, user));
        }
        return createConversation(userId, type, title, documentId, user);
    }

    private AiConversation createConversation(Long userId, String type, String title,
                                               String documentId, User user) {
        LearningMaterial material = null;
        if (documentId != null) {
            material = materialRepo.findByMaterialKey(documentId).orElse(null);
        }

        AiConversation conv = AiConversation.builder()
                .conversationKey(UUID.randomUUID().toString())
                .user(user)
                .conversationType(type)
                .title(title != null ? title : "New Conversation")
                .document(material)
                .build();

        return conversationRepo.save(conv);
    }

    private void addMessage(AiConversation conversation, String role, String content) {
        AiMessage msg = AiMessage.builder()
                .conversation(conversation)
                .role(role)
                .content(content)
                .demo(true)
                .build();
        conversation.getMessages().add(msg);
        conversationRepo.save(conversation);
    }

    private User getUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    private ConversationResponse toConversationResponse(AiConversation conv) {
        List<ConversationResponse.MessageResponse> messages = conv.getMessages().stream()
                .map(m -> ConversationResponse.MessageResponse.builder()
                        .id(m.getId())
                        .role(m.getRole())
                        .content(m.getContent())
                        .demo(m.isDemo())
                        .createdAt(m.getCreatedAt())
                        .build())
                .toList();

        return ConversationResponse.builder()
                .conversationId(conv.getConversationKey())
                .title(conv.getTitle())
                .conversationType(conv.getConversationType())
                .messages(messages)
                .createdAt(conv.getCreatedAt())
                .updatedAt(conv.getUpdatedAt())
                .build();
    }
}

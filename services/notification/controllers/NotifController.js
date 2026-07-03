 
 
 const notifAlert = async (req, res) => {
    try {
        const { userId, message } = req.body;
        // Logic to send notification to the user (e.g., via email, push notification, etc.)
        console.log(`Sending notification to user ${userId}: ${message}`);
        res.status(200).json({ success: true, message: "Notification sent successfully" });
    } catch (error) {
        console.error("Error sending notification:", error);
        res.status(500).json({ success: false, message: "Failed to send notification" });
    }
};

module.exports = { notifAlert };
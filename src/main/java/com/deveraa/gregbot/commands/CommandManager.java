package com.deveraa.gregbot.commands;

import com.deveraa.gregbot.lavaplayer.GuildMusicManager;
import com.deveraa.gregbot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        final AudioManager audioManager = event.getGuild().getAudioManager();
        final VoiceChannel memberChannel = (VoiceChannel) event.getMember().getVoiceState().getChannel();

        String command = event.getName();
        String member = event.getMember().getAsMention();

        if (command.equals("play")) {
            if(!event.getMember().getVoiceState().inAudioChannel()) {
                event.deferReply().queue();
                event.getHook().sendMessage(member + " `Ur not in vc?`").queue();
            } else {
                audioManager.openAudioConnection(memberChannel);

                OptionMapping songOption = event.getOption("song");
                String song = songOption.getAsString();

                if (song.contains("https://")) {
                    PlayerManager.getINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), song);
                    event.deferReply().queue();
                    event.getHook().sendMessage(member + " `Queued a song via URL: `" + song).queue();
                } else {
                    String link = "ytsearch:" + song + " audio";
                    PlayerManager.getINSTANCE().loadAndPlay(event.getChannel().asTextChannel(), link);
                    event.deferReply().queue();
                    event.getHook().sendMessage(member + " `Queueing song with title: " + song + ".`").queue();
                }
                final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(memberChannel.getGuild());
                musicManager.audioPlayer.setVolume(30);
            }
        }

        if (command.equals("skip")) {
            if(!event.getMember().getVoiceState().inAudioChannel()) {
                event.deferReply().queue();
                event.getHook().sendMessage(member + " `Ur not in vc?`").queue();
            } else {
                final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(memberChannel.getGuild());
                 if (musicManager.audioPlayer.getPlayingTrack() == null) {
                     event.deferReply().queue();
                     event.getHook().sendMessage("`There's nothing playing...`").queue();
                 } else {
                     musicManager.scheduler.nextTrack();

                     event.deferReply().queue();
                     event.getHook().sendMessage("`Okay next...`").queue();
                 }
            }
        }

        if (command.equals("clear")) {
            if(!event.getMember().getVoiceState().inAudioChannel()) {
                event.deferReply().queue();
                event.getHook().sendMessage(member + " `Ur not in vc?`").queue();
            } else {
                final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(memberChannel.getGuild());
                musicManager.scheduler.audioPlayer.stopTrack();
                musicManager.scheduler.queue.clear();
                PlayerManager.getINSTANCE().clearQueue();

                event.deferReply().queue();
                event.getHook().sendMessage("`Clearing queue...`").queue();
            }
        }

        if (command.equals("stop")) {
            if(!event.getMember().getVoiceState().inAudioChannel()) {
                event.deferReply().queue();
                event.getHook().sendMessage(member + " `Ur not in vc?`").queue();
            } else {
                final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(memberChannel.getGuild());
                musicManager.scheduler.audioPlayer.stopTrack();
                musicManager.scheduler.queue.clear();
                audioManager.closeAudioConnection();
                PlayerManager.getINSTANCE().clearQueue();

                event.deferReply().queue();
                event.getHook().sendMessage("`Greg has left the chat.`").queue();
            }
        }

        if (command.equals("vol")) {
            if(!event.getMember().getVoiceState().inAudioChannel()) {
                event.deferReply().queue();
                event.getHook().sendMessage(member + " `Ur not in vc?`").queue();
            } else {
                OptionMapping volumeOption = event.getOption("volume");
                int volume = volumeOption.getAsInt();
                final GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(memberChannel.getGuild());
                System.out.println("Volume is: " + musicManager.audioPlayer.getVolume());
                event.deferReply().queue();
                event.getHook().sendMessage("`Set volume to " + volume + "`").queue();
            }
        }

        if (command.equals("song")) {
            if(!event.getMember().getVoiceState().inAudioChannel()) {
                event.deferReply().queue();
                event.getHook().sendMessage(member + " `Ur not in vc?`").queue();
            } else {
                event.deferReply().queue();
                event.getHook().sendMessage("`Currently playing: " + PlayerManager.getTitle() + " by " + PlayerManager.getAuthor() + ".`").queue();
            }
        }

        if (command.equals("list")) {
            ArrayList<String> queue = PlayerManager.getQueue();
            if (queue.isEmpty()) {
                event.deferReply().queue();
                event.getHook().sendMessage("`Nothing in queue!`").queue();
            } else {
                StringBuilder list = new StringBuilder();
                for (int i = 0 ; i < queue.size() ; i++) {
                    list.append(i + 1).append(" : ").append(queue.get(i)).append("\n");
                }
                event.deferReply().queue();
                event.getHook().sendMessage("`" + list + "`").queue();
            }

        }

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        OptionData song = new OptionData(OptionType.STRING, "song", "play a bop >.<", true);
        commandData.add(Commands.slash("play", "Plays any bop you want!").addOptions(song));

        OptionData volume = new OptionData(OptionType.STRING, "volume", "0-100 (100 very loud!)", true);
        commandData.add(Commands.slash("vol", "Set the volume!").addOptions(volume));

        commandData.add(Commands.slash("skip", "Skips a song >.>"));
        commandData.add(Commands.slash("clear", "Clears all the music! >.<"));
        commandData.add(Commands.slash("stop", "ALT + F4'ing lol"));
        commandData.add(Commands.slash("song", "What song is this?"));

        commandData.add(Commands.slash("list", "Listing all the songs in queue :3"));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
